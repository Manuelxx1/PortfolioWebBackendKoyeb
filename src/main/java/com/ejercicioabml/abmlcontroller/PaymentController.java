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

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

import com.mercadopago.client.common.PayerRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

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
    private OrderItemsRepository orderItemsRepository; // agregado
    
    public PaymentController() {
        MercadoPagoConfig.setAccessToken("APP_USR-4456023071312309-111404-da075421e24ad80c6ba26beb86c2e77a-2989163784");
        this.paymentClient = new PaymentClient();
        this.preferenceClient = new PreferenceClient();
    }

    // Crear preferencia de pago

// Crear preferencia y orden con cantidad dinámica
@PostMapping("/create/{productId}")
public ResponseEntity<String> createPreference(
        @PathVariable Long productId,
        @RequestBody(required = false) CompraRequest body) {
    try {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int quantity = (body != null && body.getQuantity() > 0) ? body.getQuantity() : 1;

        if (product.getStock() < quantity) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Stock insuficiente. Disponible: " + product.getStock());
        }

        // Crear ítem para la preferencia
        Item item = new Item();
item.setTitle(product.getName() + " (x" + quantity)");
item.setQuantity(quantity);
item.setUnitPrice(product.getPrice().doubleValue());

        // Crear preferencia
        Preference preference = new Preference();
        preference.appendItem(item);

        // Buscar usuario
        Users usuario = null;
        if (body != null && body.getUsuario() != null) {
            usuario = userRepository.findByUsername(body.getUsuario()).orElse(null);
        }

        if (usuario == null) {
            usuario = userRepository.findByUsername("guest")
                    .orElseGet(() -> {
                        Users newGuest = new Users();
                        newGuest.setUsername("guest");
                        newGuest.setEmail("guest@example.com");
                        newGuest.setName("Usuario Genérico");
                        newGuest.setPassword("guest");
                        return userRepository.save(newGuest);
                    });
        }

        // Crear orden interna
        Orders order = new Orders();
        order.setProductName(product.getName());
        order.setStatus("pending");
        order.setUser(usuario);
        order.setTotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        orderRepository.save(order);

        // Guardar ítem vinculado
        OrderItems orderItem = new OrderItems();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setProductName(product.getName());
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice());
        orderItem.setAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        orderItemsRepository.save(orderItem);

        // Configurar payer en la preferencia
        Payer payer = new Payer();
        payer.setName(usuario.getName());
        payer.setEmail(usuario.getEmail());
        preference.setPayer(payer);

        // External reference = ID de la orden
        preference.setExternalReference(order.getId().toString());

        // Notification URL
        preference.setNotificationUrl("https://portfoliowebbackendkoyeb-1-ulka.onrender.com/api/payments/webhook");

        // Guardar preferencia en MP
        preference.save();

        // Guardar preferenceId real en la orden
        order.setPreferenceId(preference.getId());
        orderRepository.save(order);

        return ResponseEntity.ok(preference.getInitPoint());

    } catch (Exception e) {
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

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            totalCarrito = totalCarrito.add(itemTotal);

            // Cada ítem con cantidad y precio unitario
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    //.title(product.getName())
                   // .quantity(ci.getQuantity())
                   // .unitPrice(product.getPrice())
                  //  .currencyId("ARS") // importante: especificar moneda
                   // .build();
                .title(product.getName()) // Enviar solo el nombre del producto
    .quantity(ci.getQuantity()) // Enviar la cantidad REAL (ej. 3)
    .unitPrice(product.getPrice()) // Enviar el precio UNITARIO REAL (ej. $5000)
    .currencyId("ARS")
    .build();

            items.add(item); // agregar ítem a la lista
      
        
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

                    // Buscar o crear usuario
                    Users user = null;
                    if (payment.getPayer() != null && payment.getPayer().getId() != null) {
                        Long mpUserId = Long.parseLong(payment.getPayer().getId());
                        user = userRepository.findByMpUserId(mpUserId)
                                .orElseGet(() -> {
                                    Users newUser = new Users();
                                    newUser.setMpUserId(mpUserId);

                                    String email = payment.getPayer().getEmail();
                                    String firstName = payment.getPayer().getFirstName();

                                    if (email != null && !email.isEmpty()) {
                                        newUser.setUsername(email);
                                        newUser.setEmail(email);
                                    } else {
                                        newUser.setUsername("mpuser_" + mpUserId);
                                        newUser.setEmail("sin-email@mercadopago.com");
                                    }

                                    if (firstName != null && !firstName.isEmpty()) {
                                        newUser.setName(firstName);
                                    } else {
                                        newUser.setName("Cliente Mercado Pago");
                                    }

                                    newUser.setPassword("mercadopago");
                                    return userRepository.save(newUser);
                                });
                    }

                    // Actualizar orden
                    order.setUser(user);
                    order.setStatus(payment.getStatus());
                    order.setTotal(payment.getTransactionAmount());

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
}
