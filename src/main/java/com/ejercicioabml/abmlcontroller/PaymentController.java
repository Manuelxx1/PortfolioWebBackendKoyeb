package com.ejercicioabml.abmlcontroller;

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
            String paymentId = data.get("id").toString();

            Payment payment = paymentClient.get(paymentId);

            // Ejemplo: guardar en tu tabla orders
            // ASUME que existe la clase Orders y orderRepository
            
            Orders orders = new Orders();
            orders.setId(payment.getId());
            orders.setProductName(payment.getDescription());
            orders.setAmount(payment.getTransactionAmount().intValue());
            orders.setStatus(payment.getStatus()); // "approved", "pending", "rejected"

            orderRepository.save(orders);
            
            
            // Simulación de procesamiento
            System.out.println("Pago recibido. ID de pago: " + payment.getId() + ", Estado: " + payment.getStatus());


            return ResponseEntity.ok("Webhook procesado correctamente");
        } catch (Exception e) {
            System.err.println("Error procesando webhook: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando webhook: " + e.getMessage());
        }
    }
}
