package com.ejercicioabml.abmlcontroller;

import com.abml.jpa.hibernate.model.Users;
import com.abml.jpa.hibernate.model.Orders;
import com.abml.jpa.hibernate.model.OrderItems;
import com.abml.jpa.hibernate.model.Product;
import com.abml.jpa.hibernate.dto.CheckoutCarritoRequest;
import com.abml.jpa.hibernate.dto.CompraRequest;
import com.abml.jpa.hibernate.dto.PagoDTO;
import com.abml.jpa.hibernate.service.CartService;

import com.abml.jpa.hibernate.repository.UserRepository;
import com.abml.jpa.hibernate.repository.OrderRepository;
import com.abml.jpa.hibernate.repository.OrderItemsRepository;
import com.abml.jpa.hibernate.repository.ProductRepository;

// Importaciones de Mercado Pago (v2.5.0) - CORRECTAS
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.payment.Payment; 

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;    // <--- ESTA ES LA IMPORTANTE

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;
import java.util.HashMap;

import java.math.BigDecimal;


import java.util.ArrayList;

import java.util.List;
import java.util.Collections; 
import java.util.Optional;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "https://4200-cs-582739288523-default.cs-us-east1-pkhd.cloudshell.dev")
public class PaymentController {

    private final PaymentClient paymentClient;
    private final PreferenceClient preferenceClient;
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private CartService cartService;
    
    public PaymentController() {
        String accessToken = System.getenv("MERCADOPAGO_ACCESS_TOKEN");
MercadoPagoConfig.setAccessToken(accessToken);

        
        this.paymentClient = new PaymentClient();
        this.preferenceClient = new PreferenceClient();
    }

    // Crear preferencia y orden con cantidad dinámica
    @PostMapping("/create/{productId}")
    public ResponseEntity<String> createPreference(
            @PathVariable Long productId,
            @RequestBody(required = false) CompraRequest compraRequestDTO) {
        try {

            // Mostrar lo que llega al DTO
        System.out.println("CompraRequest recibido: " + compraRequestDTO);
            System.out.println("DTO completo: " + compraRequestDTO);
System.out.println("shippingType: " + compraRequestDTO.getShippingType());
System.out.println("shippingCost: " + compraRequestDTO.getShippingCost());
System.out.println("shippingName: " + compraRequestDTO.getShippingName());

            // 1. Buscar producto y verificar stock
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int quantity = (compraRequestDTO != null && compraRequestDTO.getQuantity() > 0) ? compraRequestDTO.getQuantity() : 1;

        if (product.getStock() < quantity) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Stock insuficiente. Disponible: " + product.getStock());
        }

        // 2. Buscar usuario por idUsuario
Users usuario = userRepository.findById(compraRequestDTO.getIdUsuario())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

System.out.println("Usuario encontrado: " + usuario);



        // 3. Crear orden interna
        Orders order = new Orders();
        order.setProductName(product.getName());
        order.setStatus("pending");
        order.setUser(usuario);
order.setName(compraRequestDTO.getName());
order.setEmail(compraRequestDTO.getEmail());
order.setPhone(compraRequestDTO.getPhone());
order.setAddress(compraRequestDTO.getAddress());
order.setCity(compraRequestDTO.getCity());
order.setPostalCode(compraRequestDTO.getPostalCode());

order.setShippingType(compraRequestDTO.getShippingType());
order.setShippingCost(compraRequestDTO.getShippingCost());
order.setShippingName(compraRequestDTO.getShippingName());

            // Calcular total con envío 
            BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(quantity));

if (compraRequestDTO.getShippingCost() > 0) {
    total = total.add(BigDecimal.valueOf(compraRequestDTO.getShippingCost()));
}

order.setTotal(total);

            

            
        // Primero guardamos para que tenga un id 
            orderRepository.save(order); 
            // Ahora seteamos el campo  externalReference con ese id 
            order.setExternalReference(order.getId().toString()); 
            orderRepository.save(order);
            
            // --- CONFIGURACIÓN DE MERCADO PAGO (VERSIÓN 2.5.0) ---
            
            // 4. Crear Ítem Request para el producto usando el Builder
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .title(product.getName() + " (x " + quantity + ")")
                .quantity(quantity)
              //  precio unitario del producto
                .unitPrice(product.getPrice()) // product.getPrice() ya es BigDecimal

                .build();

            // 4.1. Crear Ítem Request para el envío 
            PreferenceItemRequest shippingItem = PreferenceItemRequest.builder() .title("Costo de envío - " + compraRequestDTO.getShippingName())
                .quantity(1) //porque es las veces que se cobra el envío 
                .unitPrice(BigDecimal.valueOf(compraRequestDTO.getShippingCost())) // costo del envío
                .build();

            // 5. Crear Payer Request usando el Builder
            PreferencePayerRequest payerRequest = PreferencePayerRequest.builder()
                .name(usuario.getName())
                .email(usuario.getEmail())
                .build();

            // 6. Crear la Preference Request usando el Builder
// 1. Crear el objeto de Back URLs por separado
PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
    .success("https://4200-cs-582739288523-default.cs-us-east1-pkhd.cloudshell.dev/estado-compra")
    .failure("https://4200-cs-582739288523-default.cs-us-east1-pkhd.cloudshell.dev/estado-compra")
    .pending("https://4200-cs-582739288523-default.cs-us-east1-pkhd.cloudshell.dev/estado-compra")
    .build();

// 2. Usar ambos ítems en la PreferenceRequest
PreferenceRequest preferenceRequest = PreferenceRequest.builder()
.items(List.of(itemRequest, shippingItem)) // producto + envio más limpio y moderno es List.of que Arrays.asList 
    .payer(payerRequest)
    .externalReference(order.getExternalReference()) // ahora usa el valor guardado
    .notificationUrl("https://portfoliowebbackendkoyeb-1-ulka.onrender.com/api/payments/webhook")
    .backUrls(backUrls) // <-- Aquí pasas el objeto ya construido
    .autoReturn("all")
    .build();


            // 7. Crear la preferencia usando el Cliente
            Preference preference = preferenceClient.create(preferenceRequest);
            
            // --- FIN DE CONFIGURACIÓN DE MERCADO PAGO ---

            // 8. Guardar ítem de la orden (OrderItems)
            OrderItems orderItem = new OrderItems();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice());
            
            // LÍNEA 149 (Aprox.) - CORRECCIÓN: Cálculo directo (esto arregla el 'cannot find symbol')
            orderItem.setAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity))); 
            
            orderItemsRepository.save(orderItem);

            // 9. Guardar preferenceId en la orden (Actualización)
            order.setPreferenceId(preference.getId());
            orderRepository.save(order);

            // 10. Retornar InitPoint
            return ResponseEntity.ok(preference.getInitPoint());

        } catch (Exception e) {
            log.error("Error creando preferencia para productId: " + productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creando preferencia: " + e.getMessage());
        }
    }


    //compra desde el carrito 
@PostMapping("/comprarCarrito")
    public ResponseEntity<String> comprarCarrito(@RequestBody CheckoutCarritoRequest request) {
        // Buscar usuario
      try {
        Users user = userRepository.findById(request.getIdUsuario())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Pasar todo al servicio
        String initPoint = cartService.comprarCarrito(
            user,
            request.getItems(),
            request.getName(),
            request.getEmail(),
            request.getPhone(),
            request.getAddress(),
            request.getCity(),
            request.getPostalCode(),
            request.getShippingType(),
            request.getShippingCost(),
            request.getShippingName()
        );

        // Devolver el initPoint para redirigir a Mercado Pago
        return ResponseEntity.ok(initPoint);
         
          /*
          se captura la RuntimeException generada por el catch en el CartService 
         y se devuelve un 500 con un mensaje claro
         para pasarlo al frontend           */
          
        } catch (RuntimeException e) {
            // Si algo falla en el servicio, devolvemos 500 con el mensaje
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al procesar la compra: " + e.getMessage());
      }
    }


    
    // Webhook de Mercado Pago sin DTO
/* @PostMapping("/webhook")
public ResponseEntity<String> webhook(@RequestBody Map<String, Object> payload) {
    System.out.println("Payload recibido en webhook: " + payload);

    try {
        String topic = (String) payload.get("topic");
        Long paymentId = null;

        if ("payment".equals(topic)) {
            if (payload.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                paymentId = Long.parseLong(data.get("id").toString());
            } else if (payload.containsKey("resource")) {
                paymentId = Long.parseLong(payload.get("resource").toString());
            }

            if (paymentId != null) {
                Payment payment = paymentClient.get(paymentId);

                System.out.println("Payment ID: " + paymentId);
                System.out.println("Payment status: " + payment.getStatus());
                System.out.println("Payment externalReference: " + payment.getExternalReference());
                System.out.println("Payment amount: " + payment.getTransactionAmount());

                String externalRef = payment.getExternalReference();
                if (externalRef == null) {
                    System.err.println("No se encontró external_reference en el pago");
                    return ResponseEntity.ok("Webhook recibido pero sin external_reference");
                }

                Orders order = orderRepository.findById(Long.parseLong(externalRef))
                        .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

                // ⚠️ IMPORTANTE: NO volver a setear order.setUser(...)
                // El usuario ya quedó vinculado en create/{productId} por idUsuario

                // Actualizar estado y monto
                order.setStatus(payment.getStatus());
                order.setTotal(payment.getTransactionAmount());

                // Guardar datos del payer de Mercado Pago
                if (payment.getPayer() != null) {
                    order.setMpPayerName(payment.getPayer().getFirstName());
                    order.setMpPayerEmail(payment.getPayer().getEmail());
                }

                // Guardar datos del usuario logueado de tu sistema
                if (order.getUser() != null) {
                    order.setLoginUsername(order.getUser().getUsername());
                    order.setLoginEmail(order.getUser().getEmail());
                }

                orderRepository.save(order);

                System.out.println("Orden actualizada en DB: " + order);
            }
        } else if ("merchant_order".equals(topic)) {
            String resourceUrl = (String) payload.get("resource");
            System.out.println("Webhook merchant_order recibido: " + resourceUrl);
        }

        return ResponseEntity.ok("Webhook procesado");

    } catch (Exception e) {
        System.err.println("Error procesando webhook: " + e.getMessage());
        return ResponseEntity.ok("Webhook recibido pero con error");
    }
}
*/

 
 /*   
@PostMapping("/webhook")
public ResponseEntity<String> webhook(@RequestBody Map<String, Object> payload) {
    System.out.println("Payload recibido en webhook: " + payload);

    try {
        String topic = (String) payload.get("topic");
        Long paymentId = null;

        if ("payment".equals(topic)) {
            if (payload.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                paymentId = Long.parseLong(data.get("id").toString());
            } else if (payload.containsKey("resource")) {
                paymentId = Long.parseLong(payload.get("resource").toString());
            }

            if (paymentId != null) {
                Payment payment = paymentClient.get(paymentId);

                System.out.println("Payment ID: " + paymentId);
                System.out.println("Payment status: " + payment.getStatus());
                System.out.println("Payment externalReference: " + payment.getExternalReference());
                System.out.println("Payment amount: " + payment.getTransactionAmount());

                String externalRef = payment.getExternalReference();
                if (externalRef == null) {
                    System.err.println("No se encontró external_reference en el pago");
                    return ResponseEntity.ok("Webhook recibido pero sin external_reference");
                }

                Orders order = orderRepository.findById(Long.parseLong(externalRef))
                        .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

                // ⚠️ IMPORTANTE: NO volver a setear order.setUser(...)
                // El usuario ya quedó vinculado en create/{productId} por idUsuario

                // Actualizar estado y monto
                order.setStatus(payment.getStatus());
                order.setTotal(payment.getTransactionAmount());
                order.setAmount(payment.getTransactionAmount()); 
                
                // Guardar datos del payer de Mercado Pago
                if (payment.getPayer() != null) {
                    order.setMpPayerName(payment.getPayer().getFirstName());
                    order.setMpPayerEmail(payment.getPayer().getEmail());
                }

                // Guardar datos del usuario logueado de tu sistema
                String destinatario = null;
                if (order.getUser() != null) {
                    order.setLoginUsername(order.getUser().getUsername());
                    order.setLoginEmail(order.getUser().getEmail());
                    destinatario = order.getUser().getEmail();
                }

                orderRepository.save(order);

                System.out.println("Orden actualizada en DB: " + order);

                // Enviar notificación por email
                if (destinatario != null) {
                    emailService.enviarNotificacion(
                        destinatario,
                        payment.getStatus(),
                        order.getProductName()
                    );
                    System.out.println("Email enviado a: " + destinatario);
                }
            }
        } else if ("merchant_order".equals(topic)) {
            String resourceUrl = (String) payload.get("resource");
            System.out.println("Webhook merchant_order recibido: " + resourceUrl);
        }

        return ResponseEntity.ok("Webhook procesado");

    } catch (Exception e) {
        System.err.println("Error procesando webhook: " + e.getMessage());
        return ResponseEntity.ok("Webhook recibido pero con error");
    }
}*/

    //para termux
@PostMapping("/webhook")
public ResponseEntity<String> webhook(@RequestBody Map<String, Object> payload) {
    System.out.println("Payload recibido en webhook: " + payload);

    try {
        String topic = (String) payload.get("topic");
        Long paymentId = null;

        if ("payment".equals(topic)) {
            if (payload.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                paymentId = Long.parseLong(data.get("id").toString());
            } else if (payload.containsKey("resource")) {
                String resource = payload.get("resource").toString();
                // A veces el resource es una URL completa, extraemos solo el ID
                paymentId = Long.parseLong(resource.replaceAll("[^0-9]", ""));
            }

System.out.println("PaymentId extraído: " + paymentId);

            if (paymentId != null) {
                // Obtener el pago desde Mercado Pago
                Payment payment = paymentClient.get(paymentId);

                System.out.println("Estado del pago en MP: " + payment.getStatus());
                System.out.println("ExternalReference recibido: " + payment.getExternalReference());
                String externalRef = payment.getExternalReference();
Orders order;
if (externalRef != null) {
    order = orderRepository.findById(Long.parseLong(externalRef))
            .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
} else {
    // Buscar por preferenceId como alternativa
    order = orderRepository.findByPreferenceId(payment.getOrder().getId().toString())
            .orElseThrow(() -> new RuntimeException("Orden no encontrada por preferenceId"));
}


                    System.out.println("Orden encontrada en DB con id: " + order.getId() + " y estado actual: " + order.getStatus());

                // 1. Actualizar estado y datos del pagador en la DB
                order.setStatus(payment.getStatus());
                order.setTotal(payment.getTransactionAmount());
                order.setAmount(payment.getTransactionAmount()); 

                             // ---  NUEVO: LÓGICA DE DESCUENTO DE STOCK ---
                // Solo descontamos si el pago es "approved" y la orden aún no estaba aprobada
                // (esto evita que si MP manda el webhook dos veces, restes el stock dos veces)
                if ("approved".equals(payment.getStatus()) && !"approved".equals(order.getStatus())) {
                    
                    System.out.println("💳 Pago aprobado. Descontando stock para la orden: " + order.getId());

                    for (OrderItems item : order.getItems()) {
                        Product product = item.getProduct();
                        int cantidadComprada = item.getQuantity();

                        if (product != null) {
                            int stockActual = product.getStock();
                            if (stockActual >= cantidadComprada) {
                                product.setStock(stockActual - cantidadComprada);
                                productRepository.save(product); // 🔹 Actualiza la tabla Products
                                System.out.println("✅ Stock restado: " + product.getName() + " (Quedan: " + product.getStock() + ")");
                            } else {
                                System.err.println("⚠️ STOCK INSUFICIENTE para: " + product.getName());
                                // Opcional: podrías poner el stock en 0 si prefieres
                                // product.setStock(0);
                                // productRepo.save(product);
                            }
                        }
                    }
                }
                // --- FIN LÓGICA DE STOCK ---

                //  NUEVO: guardar cuotas
order.setInstallments(payment.getInstallments());
if (payment.getTransactionDetails() != null) {
    order.setInstallmentAmount(payment.getTransactionDetails().getInstallmentAmount());
    order.setTotalPaidAmount(payment.getTransactionDetails().getTotalPaidAmount());
}
                
                if (payment.getPayer() != null) {
                    order.setMpPayerName(payment.getPayer().getFirstName());
                    order.setMpPayerEmail(payment.getPayer().getEmail());
                }

                String destinatario = null;
                if (order.getUser() != null) {
                    order.setLoginUsername(order.getUser().getUsername());
                    order.setLoginEmail(order.getUser().getEmail());
                    destinatario = order.getUser().getEmail();
                }

                orderRepository.saveAndFlush(order); 
                System.out.println(" Orden actualizada en DB con estado: " + order.getStatus());

                // 2. Construir el detalle de productos (Iterando los items de MP)
                StringBuilder detallesDeCompra = new StringBuilder();
                if (payment.getAdditionalInfo() != null && payment.getAdditionalInfo().getItems() != null) {
                    payment.getAdditionalInfo().getItems().forEach(item -> {
                        detallesDeCompra.append(item.getTitle())
                                       .append(" - Cantidad: ").append(item.getQuantity())
                                       .append(" - Precio: ARS ").append(item.getUnitPrice())
                                       .append("\n");
                    });
                } else {
                    String nombreSeguro = (order.getProductName() != null) ? order.getProductName() : "Productos de la orden #" + order.getId();
                    // Si MP no envía info adicional, usamos los datos locales de la orden
                    detallesDeCompra.append(nombreSeguro)
                                   .append(" - Cantidad: 1 - Precio: ARS ")
                                   .append(order.getTotal());
                }

                // 3. Enviar datos formateados a Termux
                if (destinatario != null) {
                    try {
                        RestTemplate restTemplate = new RestTemplate();
                        String urlBase = System.getenv("TERMUX_URL"); 
                        String urlTermux = urlBase + "/api/enviar-email";

                        Map<String, String> emailData = new HashMap<>();
                        emailData.put("correo", destinatario);
                        
                        // 1. Definimos variables para el estilo dinámico
String colorBorde;
String icono;
String estadoParaElUsuario;
String mensajeAyuda = ""; // Empezamos vacío

// Logica ultra-precisa de estados de Mercado Pago
String statusReal = payment.getStatus();


if ("approved".equals(statusReal)) {
    colorBorde = "#27ae60"; // Verde
    icono = "✅";
    estadoParaElUsuario = "Aprobado";
    mensajeAyuda = "¡Tu pedido ya está siendo preparado!";
} 
else if ("in_process".equals(statusReal) || "pending".equals(statusReal) || "in_mediation".equals(statusReal)) {
    colorBorde = "#f1c40f"; // Amarillo (Lo que te pasó recién)
    icono = "⏳";
    estadoParaElUsuario = "En Proceso";
    mensajeAyuda = "Te avisaremos apenas se acredite el dinero.";
} 
else {
    // Aquí caen: rejected, cancelled, refunded, charged_back
    colorBorde = "#e74c3c"; // Rojo
    icono = "❌";
    estadoParaElUsuario = "Rechazado / Cancelado";
    mensajeAyuda = "No te preocupes, puedes intentar nuevamente con otro medio de pago.";
}

        System.out.println("ESTADO RECIBIDO: " + payment.getStatus());

// Ahora usa 'estadoParaElUsuario' en el título del HTML para que no diga "in_process"

                        
                        // 1. Pon la URL de tu logo aquí
String urlLogo = "https://img.freepik.com/vector-premium/plantilla-vector-diseno-logotipo-eshop-concepto-logotipo-tienda-linea_809852-666.jpg"; 

// 2. Agrégalo al inicio del HTML
String mensajeHTML = String.format(
    "<div style='font-family: sans-serif; border: 3px solid %s; padding: 20px; border-radius: 15px;'>" +
    "   <div style='text-align: center; margin-bottom: 10px;'>" +
    "       <img src='%s' alt='nombre del eshop' style='max-width: 150px; height: auto;'>" +
    "   </div>" +
    "   <h1 style='color: %s;'> %s ¡Pago %s!</h1>" +
    "   <p style='color: #555; font-weight: bold;'>%s</p>" + // 5to %s: Mensaje de Ayuda
    "   <hr style='border: 0; border-top: 1px solid #eee;'>" +
    "   <p><strong>ID de Pago:</strong> %s</p>" +           // 6to %s: ID de Pago
    "   <div style='background: #f9f9f9; padding: 15px; border-left: 5px solid %s;'>%s</div>" + // 7mo y 8vo %s
    "   <h2 style='color: #2c3e50;'>Total: ARS %s</h2>" +   // 9no %s: Monto
    "   <p>¡Gracias por confiar en nosotros! 😊</p>" +
    "</div>",
    colorBorde,           // 1. Color del borde
    urlLogo,            // 2. URL de la imagen
    colorBorde,           // 2. Color del título
    icono,                // 3. Emoji
    estadoParaElUsuario,  // 4. Texto del estado
    mensajeAyuda,         // 5. El texto de "No te preocupes..."
    payment.getId(),      // 6. ID del pago
    colorBorde,           // 7. Color del detalle lateral
    detallesDeCompra.toString(), // 8. Lista de productos
    payment.getTransactionAmount() // 9. Monto total
);
                        
                        emailData.put("mensaje", mensajeHTML);
                        
                        // Mantengo estos por si tu script de Termux los usa por separado
                        emailData.put("estado", payment.getStatus());
                        emailData.put("producto", order.getProductName());

                        restTemplate.postForEntity(urlTermux, emailData, String.class);
                        System.out.println("Petición enviada a Termux con detalles completos");
                    } catch (Exception e) {
                        System.err.println("Error al conectar con Termux: " + e.getMessage());
                    }
                }
            }
        } else if ("merchant_order".equals(topic)) {
            System.out.println("Webhook merchant_order recibido: " + payload.get("resource"));
        }

        return ResponseEntity.ok("Webhook procesado");

    } catch (Exception e) {
        System.err.println("Error general en el webhook: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error interno");
    }
}


/*
    // Webhook de Mercado Pago usando DTO
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody PagoDTO pago) {
        try {
            System.out.println("Webhook recibido: " + pago);

            // Extraer datos del pago desde el DTO
            String estado = pago.getStatus();
            String externalRef = pago.getExternalReference();
            String producto = pago.getProductName();
            BigDecimal monto = pago.getAmount();

            System.out.println("Estado: " + estado);
            System.out.println("External Reference: " + externalRef);
            System.out.println("Monto: " + monto);

            if (externalRef == null) {
                System.err.println("No se encontró external_reference en el pago");
                return ResponseEntity.ok("Webhook recibido pero sin external_reference");
            }

            // Buscar la orden en tu DB
            Orders order = orderRepository.findById(Long.parseLong(externalRef))
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            // Actualizar estado y monto
            order.setStatus(estado);
            order.setTotal(monto);

            // Guardar datos del payer de Mercado Pago
            if (pago.getMpPayerName() != null) {
                order.setMpPayerName(pago.getMpPayerName());
                order.setMpPayerEmail(pago.getMpPayerEmail());
            }

            // Guardar datos del usuario logueado de tu sistema
            String destinatario = null;
            if (order.getUser() != null) {
                order.setLoginUsername(order.getUser().getUsername());
                order.setLoginEmail(order.getUser().getEmail());
                destinatario = order.getUser().getEmail(); // 👈 usar este para el envío
            }

            // Guardar la orden con los datos actualizados
            orderRepository.save(order);

            // Notificación por email
            if (destinatario != null) {
                emailService.enviarNotificacion(destinatario, estado, producto);
            }

            System.out.println("Orden actualizada en DB y notificación enviada: " + order);

            return ResponseEntity.ok("Webhook procesado");

        } catch (Exception e) {
            System.err.println("Error procesando webhook: " + e.getMessage());
            return ResponseEntity.ok("Webhook recibido pero con error");
        }
    
    }

*/

    
    // Ver los registros de pedidos (orders) en la base de datos
    @GetMapping("/orders")
    public List<Orders> getOrders() {
        return orderRepository.findAll();
    }

    // Endpoint para obtener una orden por ID
    @GetMapping("/orders/{id}")
    public ResponseEntity<Orders> getOrderById(@PathVariable Long id) {
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        return ResponseEntity.ok(order);
    }

//ver orders de usuarios en session login
    @GetMapping("orders/byLogin/{idUsuario}")
public List<Orders> getOrdersByLogin(@PathVariable("idUsuario") Long idUsuario) {
    System.out.println("Buscando órdenes para user_id=" + idUsuario);
    return orderRepository.findByUserId(idUsuario);
}

    //busqyeda de la orden de la compra exitosa
//usando el preferenceId 
    @GetMapping("/orders/estado/{externalReference}")
public ResponseEntity<Orders> getOrderByExternalReference(@PathVariable String externalReference) {
    return orderRepository.findByExternalReference(externalReference)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}



        }
