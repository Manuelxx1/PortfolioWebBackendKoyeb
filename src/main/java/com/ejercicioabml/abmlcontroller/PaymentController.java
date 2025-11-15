package com.ejercicioabml.abmlcontroller;

import com.abml.jpa.hibernate.model.Product;
import com.mercadopago.MercadoPago;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.Item;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    public PaymentController() {
        // Inicializá el SDK con tu Access Token de prueba (sandbox)
        try {
        MercadoPago.SDK.setAccessToken("APP_USR-4456023071312309-111404-da075421e24ad80c6ba26beb86c2e77a-2989163784
");
    } catch (com.mercadopago.exceptions.MPConfException e) {
        System.err.println("Error configurando MercadoPago: " + e.getMessage());
        }
    }

    // Endpoint para crear la preferencia de pago
    @PostMapping("/create")
    public String createPayment(@RequestBody Product product) throws Exception {
        Preference preference = new Preference();
// Configurás la URL del webhook aquí
    preference.setNotificationUrl("https://portfoliowebbackendkoyeb-1.onrender.com/api/payments/webhook");
        
        Item item = new Item();
        item.setTitle(product.getName())
            .setQuantity(1)
            // ✅ Convertimos BigDecimal a float solo aquí
            //porque el sdk de mp lo exige
            .setUnitPrice(product.getPrice().floatValue());

        preference.appendItem(item);
        preference.save();

        // Devuelve la URL de pago (init_point) para redirigir desde Angular
        return preference.getInitPoint();
    }

    // Webhook para recibir notificaciones de Mercado Pago
    @PostMapping("/webhook")
public ResponseEntity<String> webhook(@RequestBody Map<String, Object> payload) {
    System.out.println("Webhook recibido: " + payload);

    // Ejemplo: leer el estado
    String status = (String) payload.get("status");
    Integer paymentId = (Integer) payload.get("id");
Double amount = (Double) payload.get("transaction_amount");
        String productName = "Producto"; // Podés ajustar según tu lógica

        Order order = new Order();
        order.setProductName(productName);
        order.setAmount(BigDecimal.valueOf(amount));
        order.setStatus(status);

        orderRepository.save(order);
    // Aquí actualizás tu base de datos según el estado
    // pedidoService.updatePaymentStatus(paymentId, status);

    return ResponseEntity.ok("Webhook procesado");
}

}
