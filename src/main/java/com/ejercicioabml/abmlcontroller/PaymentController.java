package com.ejercicioabml.abmlcontroller;
import com.abml.jpa.hibernate.repository.OrderRepository;
import com.abml.jpa.hibernate.repository.UserRepository;
import com.abml.jpa.hibernate.model.Orders;
import com.abml.jpa.hibernate.model.Users;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
// CAMBIO IMPORTANTE AQUÍ: ItemRequest ya no existe
import com.mercadopago.client.preference.PreferenceItemRequest; 
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.math.BigDecimal;
import java.util.Arrays;
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
    private UserRepository usersRepository;

    public PaymentController() {
        MercadoPagoConfig.setAccessToken("APP_USR-4456023071312309-111404-da075421e24ad80c6ba26beb86c2e77a-2989163784");
        this.paymentClient = new PaymentClient();
        this.preferenceClient = new PreferenceClient();
    }

    // Crear preferencia de pago
    @PostMapping("/create")
    public ResponseEntity<String> createPreference() {
        try {
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title("Producto de prueba")
                    .quantity(1)
                    .unitPrice(new BigDecimal("100"))
                    .build();

            PreferenceRequest request = PreferenceRequest.builder()
                    .items(Arrays.asList(item))
                    .notificationUrl("https://portfoliowebbackendkoyeb-1.onrender.com/api/payments/webhook")
                    .build();

            Preference preference = preferenceClient.create(request);
            return ResponseEntity.ok(preference.getInitPoint());

        } catch (Exception e) {
            System.err.println("Error creando preferencia: " + e.getMessage());
            e.printStackTrace();
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
                    try {
                        Payment payment = paymentClient.get(paymentId);

                        // Buscar o crear usuario
                        
// Dentro del webhook, cuando creás el usuario nuevo:
Users user = null;
if (payment.getPayer() != null && payment.getPayer().getId() != null) {
    try {
        Long mpUserId = Long.parseLong(payment.getPayer().getId());
        user = usersRepository.findByMpUserId(mpUserId)
                .orElseGet(() -> {
                    Users newUser = new Users();
newUser.setMpUserId(mpUserId);
newUser.setEmail(payment.getPayer().getEmail());
newUser.setName(payment.getPayer().getFirstName());

// username obligatorio
if (payment.getPayer().getEmail() != null) {
    newUser.setUsername(payment.getPayer().getEmail());
} else {
    newUser.setUsername("mpuser_" + mpUserId);
}

// password obligatorio (puede ser un valor fijo si no usás login)
newUser.setPassword("mercadopago"); 

return usersRepository.save(newUser);

                });
    } catch (NumberFormatException e) {
        System.err.println("El mpUserId no es numérico: " + payment.getPayer().getId());
    }
}


                        // Crear orden asociada al usuario
                        Orders orders = new Orders();
                        orders.setProductName(payment.getDescription());
                        orders.setAmount(payment.getTransactionAmount());
                        orders.setTotal(payment.getTransactionAmount());
                        orders.setStatus(payment.getStatus());
                        if (user != null) {
                            orders.setUser(user);
                        }

                        orderRepository.save(orders);
                        System.out.println("Pago guardado en DB: " + orders);

                    } catch (Exception ex) {
                        System.err.println("No se encontró el pago con id " + paymentId + ": " + ex.getMessage());
                    }
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
