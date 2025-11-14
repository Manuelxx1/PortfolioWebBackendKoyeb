package com.ejercicioabml.abmlcontroller;

import com.mercadopago.MercadoPago;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.Item;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    public PaymentController() {
        // Inicializá el SDK con tu Access Token de prueba (sandbox)
        MercadoPago.SDK.setAccessToken("TEST-xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    }

    // Endpoint para crear la preferencia de pago
    @PostMapping("/create")
    public String createPayment(@RequestBody Product product) throws Exception {
        Preference preference = new Preference();

        Item item = new Item();
        item.setTitle(product.getName())
            .setQuantity(1)
            .setUnitPrice((float) product.getPrice());

        preference.appendItem(item);
        preference.save();

        // Devuelve la URL de pago (init_point) para redirigir desde Angular
        return preference.getInitPoint();
    }

    // Webhook para recibir notificaciones de Mercado Pago
    @PostMapping("/webhook")
    public void webhook(@RequestBody String body) {
        System.out.println("Webhook recibido: " + body);
        // Aquí marcás el pedido como pagado en tu base de datos
    }
}
