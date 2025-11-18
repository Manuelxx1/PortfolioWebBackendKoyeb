package com.ejercicioabml.abmlcontroller;
import com.abml.jpa.hibernate.model.Orders;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.preference.Item;

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

    public PaymentController() {
        // ⚠️ Usá tu Access Token de PRUEBA (sandbox)
        MercadoPagoConfig.setAccessToken("APP_USR-4456023071312309-111404-da075421e24ad80c6ba26beb86c2e77a-2989163784");
        this.paymentClient = new PaymentClient();
        this.preferenceClient = new PreferenceClient();
    }

    // Crear preferencia de pago
    @PostMapping("/create")
    public ResponseEntity<String> createPreference() {
        try {
            Item item = new Item()
                    .setTitle("Producto de prueba")
                    .setQuantity(1)
                    .setUnitPrice(new BigDecimal("100"));

            PreferenceRequest request = new PreferenceRequest()
                    .setItems(Arrays.asList(item));

            Preference preference = preferenceClient.create(request);

            return ResponseEntity.ok(preference.getInitPoint());
        } catch (Exception e) {
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
            Orders orders = new Orders();
            orders.setId(payment.getId());
            orders.setProductName(payment.getDescription());
            orders.setAmount(payment.getTransactionAmount().intValue());
            orders.setStatus(payment.getStatus()); // "approved", "pending", "rejected"

            orderRepository.save(orders);

            return ResponseEntity.ok("Webhook procesado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando webhook: " + e.getMessage());
        }
    }
}
