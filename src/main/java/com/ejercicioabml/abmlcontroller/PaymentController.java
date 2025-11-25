package com.ejercicioabml.abmlcontroller;

import com.abml.jpa.hibernate.model.Users;
import com.abml.jpa.hibernate.model.Orders;
import com.abml.jpa.hibernate.model.OrderItems;
import com.abml.jpa.hibernate.model.Product;
import com.abml.jpa.hibernate.repository.UserRepository;
import com.abml.jpa.hibernate.repository.OrderRepository;
import com.abml.jpa.hibernate.repository.ProductRepository;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "https://4200-cs-582739288523-default.cs-us-east1-yeah.cloudshell.dev")
public class PaymentController {

    private final PaymentClient paymentClient;
    private final PreferenceClient preferenceClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
private ProductRepository productRepository;

    public PaymentController() {
        MercadoPagoConfig.setAccessToken("APP_USR-4456023071312309-111404-da075421e24ad80c6ba26beb86c2e77a-2989163784");
        this.paymentClient = new PaymentClient();
        this.preferenceClient = new PreferenceClient();
    }

    // Crear preferencia de pago
@PostMapping("/create/{productId}")
public ResponseEntity<String> createPreference(@PathVariable Long productId) {
    try {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title(product.getName())
                .quantity(1)
                .unitPrice(product.getPrice())
                .build();

        // Usuario genérico para pruebas
        Users guest = userRepository.findByUsername("guest")
                .orElseGet(() -> {
                    Users newGuest = new Users();
                    newGuest.setUsername("guest");
                    newGuest.setEmail("guest@example.com");
                    newGuest.setName("Usuario Genérico");
                    newGuest.setPassword("guest");
                    return userRepository.save(newGuest);
                });

        // Crear orden interna primero
        Orders order = new Orders();
        order.setProductName(product.getName());
        order.setStatus("pending");
        order.setUser(guest);
        order.setTotal(product.getPrice()); // 👈 total inicial
        orderRepository.save(order);

        // Guardar ítems en la orden
        OrderItems orderItem = new OrderItems();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setProductName(product.getName());
        orderItem.setQuantity(1);
        orderItem.setPrice(product.getPrice());
        orderItem.setAmount(product.getPrice());
        orderItemsRepository.save(orderItem);

        // Crear preferencia con external_reference = ID de la orden interna
        PreferenceRequest request = PreferenceRequest.builder()
                .items(Arrays.asList(item))
                .notificationUrl("https://portfoliowebbackendkoyeb-1.onrender.com/api/payments/webhook")
                .externalReference(order.getId().toString()) // 👈 clave para vincular
                .build();

        Preference preference = preferenceClient.create(request);

        // Guardar el preferenceId real en la orden
        order.setPreferenceId(preference.getId());
        orderRepository.save(order);

        // Devolver el link de pago
        return ResponseEntity.ok(preference.getInitPoint());

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creando preferencia: " + e.getMessage());
    }
}





    // Webhook para recibir notificaciones de pagos
@PostMapping("/webhook")
public ResponseEntity<String> webhook(@RequestBody Map<String, Object> payload) {
    System.out.println("Payload recibido en webhook: " + payload);

    try {
        String topic = (String) payload.get("topic");
        Long paymentId = null;

        if ("payment".equals(topic)) {
            // Obtener el paymentId desde el payload
            if (payload.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                paymentId = Long.parseLong(data.get("id").toString());
            } else if (payload.containsKey("resource")) {
                paymentId = Long.parseLong(payload.get("resource").toString());
            }

            if (paymentId != null) {
                Payment payment = paymentClient.get(paymentId);

                // Logs para depuración
                System.out.println("Payment ID: " + paymentId);
                System.out.println("Payment status: " + payment.getStatus());
                System.out.println("Payment externalReference: " + payment.getExternalReference());

                // Recuperamos el ID de la orden interna desde external_reference
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
                                }

                                newUser.setName(firstName != null ? firstName : "Desconocido");
                                newUser.setPassword("mercadopago");
                                return userRepository.save(newUser);
                            });
                }

                // Actualizar orden con usuario y estado del pago
                order.setUser(user);
                order.setStatus(payment.getStatus()); // "approved", "rejected", "cancelled", etc.
                order.calculateTotal();

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
}
