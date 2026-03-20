
package com.abml.jpa.hibernate.service;
import com.abml.jpa.hibernate.repository.ProductRepository;
import com.abml.jpa.hibernate.repository.CartItemRepository;
import com.abml.jpa.hibernate.model.CartItem;
import com.abml.jpa.hibernate.model.Users;
import com.abml.jpa.hibernate.model.Product;
import com.abml.jpa.hibernate.dto.CartItemDto;
//Notifications mediante websocket stomp
import org.springframework.messaging.simp.SimpMessagingTemplate;
//Enviar mensajes programados
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.resources.preference.Preference;



import java.util.stream.Collectors;


@Service
public class CartService {

  @Autowired private CartItemRepository cartRepo;
  @Autowired private ProductRepository productRepo;

  public List<CartItem> getCart(Users user) {
    return cartRepo.findByUser(user);
}


  //metod que agrega el producto que nos retorno
  //la búsqueda en el frontend 
  //se reciben los datos del método addToCart
  //del controller provenientes del frontend 
  //y se le asignan a este metodo 
  public void addToCart(Users user, Long productId, int quantity) {
    //se busca y se guarda el producto por id 
    Product product = productRepo.findById(productId)
      .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    //se envia los datos que deseamos
    //a los setters del model CartItem 
    CartItem item = new CartItem();
    item.setUser(user);
    item.setProduct(product);
    item.setQuantity(quantity);
    item.setAddedAt(LocalDateTime.now());
//se hace la persistencia a la tabla cart_items
    cartRepo.save(item);
  }

  //para comprar con el checkout de MP
public String comprarCarrito(Users user,
                                 List<CartItemDto> items,
                                 String name,
                                 String email,
                                 String phone,
                                 String address,
                                 String city,
                                 String postalCode,
                                 String shippingType,
                                 BigDecimal shippingCost,
                                 String shippingName) {

try {

  
        // Configurar credenciales de Mercado Pago
String accessToken = System.getenv("MERCADOPAGO_ACCESS_TOKEN");
MercadoPagoConfig.setAccessToken(accessToken);
        // Convertir los items del carrito a items de Mercado Pago
        List<PreferenceItemRequest> mpItems = items.stream()
        .map(i -> PreferenceItemRequest.builder()
                .title(getProductName(i.getProductId()))
                .quantity(i.getQuantity())
                .unitPrice(getProductPrice(i.getProductId())) // aquí va directo
                .currencyId("ARS")
                .build())
        .collect(Collectors.toList());


        // Agregar costo de envío como ítem extra si corresponde
        if (shippingCost != null && shippingCost.compareTo(BigDecimal.ZERO) > 0) {
            mpItems.add(PreferenceItemRequest.builder()
                    .title("Envío: " + shippingName)
                    .quantity(1)
                    .unitPrice(shippingCost)
                    .currencyId("ARS")
                    .build());
        }

        // Crear preferencia con datos del comprador
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(mpItems)
                .payer(PreferencePayerRequest.builder()
                        .name(name)
                        .email(email)
                        .build())
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        // initPoint es la URL de checkout de Mercado Pago
        return preference.getInitPoint();
    
  } catch (com.mercadopago.exceptions.MPException e) {
        // Manejo de error: loguear y devolver algo apropiado
        throw new RuntimeException("Error al crear preferencia en Mercado Pago", e);
}

    private String getProductName(Long productId) {
        // Recuperar nombre del producto desde DB
        return "Producto " + productId;
    }

    private BigDecimal getProductPrice(Long productId) {
    // Recuperar precio del producto desde DB
    return BigDecimal.valueOf(100.0); // ejemplo
}

  
  // /increase → botón +
    public void increaseFromCart(Users user, Long productId) {
        CartItem item = cartRepo.findByUser(user).stream()
            .filter(i -> i.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        item.setQuantity(item.getQuantity() + 1);
        cartRepo.save(item);
    }

    // /decrease → botón −
public void decreaseFromCart(Users user, Long productId) {
    CartItem item = cartRepo.findByUser(user).stream()
        .filter(i -> i.getProduct().getId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Item no encontrado"));

    if (item.getQuantity() > 1) {
        item.setQuantity(item.getQuantity() - 1);
        cartRepo.save(item);
    } else {
        // si llega a 0, lo eliminamos del carrito
        cartRepo.delete(item);
    }
}

    // /remove/{id}
    public void removeFromCart(Users user, Long cartItemId) {
        CartItem item = cartRepo.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No autorizado");
        }

        cartRepo.delete(item);
    }

    // /clear
    public void clearCart(Users user) {
        List<CartItem> items = cartRepo.findByUser(user);
        cartRepo.deleteAll(items);
    }


  @Autowired private SimpMessagingTemplate template;
  // cuando haya pasado 24 sin actividad en el carrito 
//Se ejecuta el Scheduled cada hora 
  //para recordarle al usuario que tiene productos en su carrito
@Scheduled(fixedRate = 3600000) 
  //se ejecuta cada 10 segundos para probar
 // @Scheduled(fixedRate = 10000) 
  //se ejecuta cada 1 minuto para probar
  //@Scheduled(fixedRate = 60000)
  public void enviarRecordatorios() { 
    // Carritos con items agregados hace más de 24 horas
   LocalDateTime limite = LocalDateTime.now().minusHours(24);
   //LocalDateTime limite = LocalDateTime.now().minusMinutes(1);
    //pruebas inmediatas
    //LocalDateTime limite = LocalDateTime.now().minusSeconds(30);

    List<CartItem> abandonados = cartRepo.findByAddedAtBefore(limite); 
    for (CartItem item : abandonados) { 
      //item.getProduct().getName() llamado desde CartItem por relación entre entidad  CartItem  y product     
      String mensaje = "⏰ Recordatorio: aún tienes " + item.getQuantity() + " unidad(es) de '" + item.getProduct().getName() + "' en tu carrito";
      template.convertAndSend("/topic/notificaciones", mensaje); 
    }
  }

}
