package com.ejercicioabml.abmlcontroller;

import com.abml.jpa.hibernate.model.Users;
import com.abml.jpa.hibernate.model.Orders;
import com.abml.jpa.hibernate.model.OrderItems;
import com.abml.jpa.hibernate.model.Product;
import com.abml.jpa.hibernate.dto.CartItemDto;
import com.abml.jpa.hibernate.dto.CompraRequest;
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
@CrossOrigin(origins = "https://4200-cs-582739288523-default.cs-us-east1-yeah.cloudshell.dev")
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
    
    public PaymentController() {
        MercadoPagoConfig.setAccessToken("APP_USR-4456023071312309-111404-da075421e24ad80c6ba26beb86c2e77a-2989163784");
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
        order.setTotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

        // Primero guardamos para que tenga un id 
            orderRepository.save(order); 
            // Ahora seteamos el campo  externalReference con ese id 
            order.setExternalReference(order.getId().toString()); 
            orderRepository.save(order);
            
            // --- CONFIGURACIÓN DE MERCADO PAGO (VERSIÓN 2.5.0) ---
            
            // 4. Crear Ítem Request usando el Builder
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .title(product.getName() + " (x " + quantity + ")")
                .quantity(quantity)
                .unitPrice(product.getPrice()) // product.getPrice() ya es BigDecimal

                .build();

            // 5. Crear Payer Request usando el Builder
            PreferencePayerRequest payerRequest = PreferencePayerRequest.builder()
                .name(usuario.getName())
                .email(usuario.getEmail())
                .build();

            // 6. Crear la Preference Request usando el Builder
// 1. Crear el objeto de Back URLs por separado
PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
    .success("https://4200-cs-582739288523-default.cs-us-east1-yeah.cloudshell.dev/estado-compra")
    .failure("https://4200-cs-582739288523-default.cs-us-east1-yeah.cloudshell.dev/estado-compra")
    .pending("https://4200-cs-582739288523-default.cs-us-east1-yeah.cloudshell.dev/estado-compra")
    .build();

// 2. Usarlo en la PreferenceRequest
PreferenceRequest preferenceRequest = PreferenceRequest.builder()
    .items(Collections.singletonList(itemRequest))
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
    @PostMapping("/create-cart")
    public ResponseEntity<String> createCartPreference(@RequestBody List<CartItemDto> cartItems) {
        try {
            log.info("CartItems recibidos: {}", cartItems);

            if (cartItems == null || cartItems.isEmpty()) {
                return ResponseEntity.badRequest().body("El carrito está vacío");
            }

            List<PreferenceItemRequest> items = new ArrayList<>();
            BigDecimal totalCarrito = BigDecimal.ZERO;

            for (CartItemDto ci : cartItems) {
                Product product = productRepository.findById(ci.getProductId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + ci.getProductId()));

                if (product.getStock() < ci.getQuantity()) {
                    return ResponseEntity.badRequest()
                            .body("Stock insuficiente para " + product.getName());
                }

                // Cálculo intermedio para el total del carrito, usa BigDecimal.valueOf(int)
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
                totalCarrito = totalCarrito.add(itemTotal);

                // Cada ítem con cantidad y precio unitario para Mercado Pago (usa double)
                PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title(product.getName())
                    .quantity(ci.getQuantity())
                    .unitPrice(product.getPrice()) // 
                    .currencyId("ARS")
                    .build();

                items.add(item); 
            }

            // Usuario genérico
            Users guestUser = userRepository.findByUsername("guest")
                    .orElseThrow(() -> new RuntimeException("Usuario genérico no encontrado"));

            Orders order = new Orders();
            order.setProductName("Carrito de compra");
            order.setStatus("pending");
            order.setTotal(totalCarrito); 
            order.setUser(guestUser);
            orderRepository.save(order);

            // Guardar ítems de la orden
            for (CartItemDto ci : cartItems) {
                Product product = productRepository.findById(ci.getProductId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + ci.getProductId()));

                OrderItems oi = new OrderItems();
                oi.setOrder(order);
                oi.setProduct(product);
                oi.setProductName(product.getName());
                oi.setQuantity(ci.getQuantity());
                oi.setPrice(product.getPrice());
                
                // LÍNEA 198 (Aprox.) - CORRECCIÓN: Cálculo directo y seguro
                oi.setAmount(product.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
                
                orderItemsRepository.save(oi);
            }

            // Crear preferencia en MercadoPago
            PreferenceRequest request = PreferenceRequest.builder()
                    .items(items)
                    .notificationUrl("https://portfoliowebbackendkoyeb-1-ulka.onrender.com/api/payments/webhook")
                    .externalReference(order.getId().toString())
                    .build();

            Preference preference = preferenceClient.create(request);

            log.info("Respuesta de MercadoPago: {}", preference);
            log.info("Preference ID: {}", preference.getId());
            log.info("InitPoint: {}", preference.getInitPoint());

            order.setPreferenceId(preference.getId());
            orderRepository.save(order);

            return ResponseEntity.ok(preference.getInitPoint());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creando preferencia de carrito: " + e.getMessage());
        }
    }

// Webhook de Mercado Pago
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
