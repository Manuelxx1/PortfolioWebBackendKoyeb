package com.ejercicioabml.abmlcontroller;
import com.abml.jpa.hibernate.repository.OrderRepository;
import com.abml.jpa.hibernate.model.Orders;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
// CAMBIO IMPORTANTE AQUÍ: ItemRequest ya no existe
import com.mercadopago.client.preference.PreferenceItemRequest; 
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentClient paymentClient;
    private final PreferenceClient preferenceClient;
    
    // NOTA: Para que el webhook funcione, DEBES inyectar un repository aquí.
    private final OrderRepository orderRepository; 

    public PaymentController() {
        // Usá tu Access Token de PRUEBA (sandbox)
        MercadoPagoConfig.setAccessToken("APP_USR-4456023071312309-111404-da075421e24ad80c6ba26beb86c2e77a-2989163784");
        this.paymentClient = new PaymentClient();
        this.preferenceClient = new PreferenceClient();
        // this.orderRepository = orderRepository; // Inyectar mediante constructor o @Autowired
    }

    // Crear preferencia de pago
    @PostMapping("/create")
    public ResponseEntity<String> createPreference() {
        try {
            // CAMBIO IMPORTANTE AQUÍ: Usamos PreferenceItemRequest
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title("Producto de prueba")
                    .quantity(1)
                    .unitPrice(new BigDecimal("100"))
                    .build();

            PreferenceRequest request = PreferenceRequest.builder()
                    .items(Arrays.asList(item))
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
    try {
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        String paymentIdPayload = data.get("id").toString(); // Lo dejamos para ver qué ID recibimos del webhook

        Payment payment = paymentClient.get(paymentIdPayload);

        // Ejemplo: guardar en tu tabla orders
        Orders orders = new Orders();
        
        // CORRECCIÓN 1: Usar el Long ID del objeto Payment
        // El ID del Payment de MP es un Long
        orders.setId(payment.getId()); 
        
        orders.setProductName(payment.getDescription());
        
        // CORRECCIÓN 2: Usar el BigDecimal para el monto (getTransactionAmount())
        // El TransactionAmount ya es un BigDecimal
        orders.setAmount(payment.getTransactionAmount()); 
        
        orders.setStatus(payment.getStatus()); // "approved", "pending", "rejected"

        orderRepository.save(orders); // Descomentar cuando OrderRepository esté inyectado y Orders esté definida

        return ResponseEntity.ok("Webhook procesado correctamente");
    } catch (Exception e) {
        // ... manejo de error
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error procesando webhook: " + e.getMessage());
    }
    }
}
