package com.ejercicioabml.abmlcontroller;

import com.abml.jpa.hibernate.model.Product;
import com.abml.jpa.hibernate.model.Orders;
import com.mercadopago.MercadoPago;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.Item;
import org.springframework.web.bind.annotation.*;
import com.abml.jpa.hibernate.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {
private final OrderRepository orderRepository;
    public PaymentController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        // Inicializá el SDK con tu Access Token de prueba (sandbox)
        try {
        MercadoPago.SDK.setAccessToken("APP_USR-4456023071312309-111404-da075421e24ad80c6ba26beb86c2e77a-2989163784");
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
public ResponseEntity<Long> webhook(@RequestBody Map<String, Object> payload) {
    System.out.println("Webhook recibido: " + payload);

    // Ejemplo: leer el estado
    String status = (String) payload.get("status");
    Integer paymentId = (Integer) payload.get("id");
Double amount = (Double) payload.get("transaction_amount");
        String productName = "Producto"; // Podés ajustar según tu lógica

        Orders orders = new Orders();
        orders.setProductName(productName);
        orders.setAmount(BigDecimal.valueOf(amount));
        orders.setStatus(status);

    Orders savedOrder = orderRepository.save(orders);

    // devolvemos el ID del pedido recién guardado
    return ResponseEntity.ok(savedOrder.getId());
}

    //muestra si el pedido u orders 
    //que se registro en la tabla orders 
    //fue procesado correctamente 
    
@GetMapping("/orders/{id}")
public ResponseEntity<Orders> getOrder(@PathVariable Long id) {
    return orderRepository.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}

}
