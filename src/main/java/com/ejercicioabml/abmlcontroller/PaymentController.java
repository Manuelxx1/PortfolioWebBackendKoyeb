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
    //@CrossOrigin es fundamental para conectar angular con el backend Springboot
@CrossOrigin(origins = "https://4200-cs-582739288523-default.cs-us-east1-yeah.cloudshell.dev")
public class PaymentController {

    private final PaymentClient paymentClient;
    private final PreferenceClient preferenceClient;
    
    // NOTA: Para que el webhook funcione, DEBES inyectar un repository aquí.
    @Autowired
    private OrderRepository orderRepository;
   
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
                    .notificationUrl("https://portfoliowebbackendkoyeb-1.onrender.com/api/payments/webhook") //  Aquí tu webhook público
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
// Webhook para recibir notificaciones de pagos
 @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody Map<String, Object> payload) {
        System.out.println("Payload recibido en webhook: " + payload);

        try {
            String topic = (String) payload.get("topic");
            Long paymentId = null;

            if ("payment".equals(topic)) {
                // Puede venir con data o con resource
                if (payload.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) payload.get("data");
                    paymentId = Long.parseLong(data.get("id").toString());
                } else if (payload.containsKey("resource")) {
                    paymentId = Long.parseLong(payload.get("resource").toString());
                }

                if (paymentId != null) {
                    try {
                        Payment payment = paymentClient.get(paymentId);

                        Orders orders = new Orders();
                        orders.setProductName(payment.getDescription());
                        orders.setAmount(payment.getTransactionAmount());
                        orders.setStatus(payment.getStatus());

                        // Conversión de String a Long para el userId
                        if (payment.getPayer() != null && payment.getPayer().getId() != null) {
                            try {
                                orders.setUserId(Long.parseLong(payment.getPayer().getId()));
                            } catch (NumberFormatException e) {
                                System.err.println("El userId no es numérico: " + payment.getPayer().getId());
                            }
                        }

                        //Seteamos el campo total (igual al amount)
orders.setTotal(payment.getTransactionAmount());

                        orderRepository.save(orders);
                        System.out.println("Pago guardado en DB: " + orders);

                    } catch (Exception ex) {
                        System.err.println("No se encontró el pago con id " + paymentId + ": " + ex.getMessage());
                    }
                }

            } else if ("merchant_order".equals(topic)) {
                // Solo loguear, no guardar en DB
                String resourceUrl = (String) payload.get("resource");
                System.out.println("Webhook merchant_order recibido: " + resourceUrl);
                // Si querés, podés consultar la API de MP para obtener más info del merchant_order
            }

            return ResponseEntity.ok("Webhook recibido");
        } catch (Exception e) {
            System.err.println("Error procesando webhook: " + e.getMessage());
            return ResponseEntity.ok("Webhook recibido pero no procesado");
        }
    }

//ver los registros de pedidos u orders de la base de datos 
    @GetMapping("/orders")
public List<Orders> getOrders() {
    return orderRepository.findAll();
}


}
