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

        PreferenceRequest request = PreferenceRequest.builder()
                .items(Arrays.asList(item))
                .notificationUrl("https://portfoliowebbackendkoyeb-1.onrender.com/api/payments/webhook")
            .metadata(Map.of("preference_id", "tuPreferenceId"))   
                .build();

        Preference preference = preferenceClient.create(request);

        //  Usuario genérico para pruebas
        Users guest = userRepository.findByUsername("guest")
                .orElseGet(() -> {
                    Users newGuest = new Users();
                    newGuest.setUsername("guest");
                    newGuest.setEmail("guest@example.com");
                    newGuest.setName("Usuario Genérico");
                    newGuest.setPassword("guest");
                    return userRepository.save(newGuest);
                });

        // Crear orden inicial con preferenceId
        Orders order = new Orders();
        order.setProductName(product.getName());
        order.setStatus("pending");
        order.setPreferenceId(preference.getId());
        order.setUser(guest); //  asignamos el usuario genérico
        orderRepository.save(order);

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
            if (payload.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                paymentId = Long.parseLong(data.get("id").toString());
            } else if (payload.containsKey("resource")) {
                paymentId = Long.parseLong(payload.get("resource").toString());
            }

            if (paymentId != null) {
                Payment payment = paymentClient.get(paymentId);

                // Buscar orden por preferenceId (ya creada en /create)
                String preferenceId = payment.getMetadata().get("preference_id").toString();
                Orders order = orderRepository.findByPreferenceId(preferenceId)
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
                                    newUser.setEmail(null);
                                }

                                if (firstName != null && !firstName.isEmpty()) {
                                    newUser.setName(firstName);
                                } else {
                                    newUser.setName("Desconocido");
                                }

                                newUser.setPassword("mercadopago");
                                return userRepository.save(newUser);
                            });
                }

                // Actualizar orden con usuario y estado del pago
                order.setUser(user);
                order.setStatus(payment.getStatus());

                // Si querés recalcular el total dinámicamente con los ítems
                order.calculateTotal();

                orderRepository.save(order);

                System.out.println("Orden actualizada en DB: " + order);
            }
        } else if ("merchant_order".equals(topic)) {
            String resourceUrl = (String) payload.get("resource");
            System.out.println("Webhook merchant_order recibido: " + resourceUrl);
        }

        return ResponseEntity.ok("Webhook recibido");

    } catch (Exception e) {
        System.err.println("Error procesando webhook: " + e.getMessage());
        return ResponseEntity.ok("Webhook recibido pero no procesado");
    }
}


    // Ver los registros de pedidos (orders) en la base de datos
    @GetMapping("/orders")
    public List<Orders> getOrders() {
        return orderRepository.findAll();
    }
}
