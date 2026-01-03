
package com.abml.jpa.hibernate.service;
import com.abml.jpa.hibernate.repository.ProductRepository;
import com.abml.jpa.hibernate.repository.CartItemRepository;
import com.abml.jpa.hibernate.model.CartItem;
import com.abml.jpa.hibernate.model.Users;
import com.abml.jpa.hibernate.model.Product;
//Notifications mediante websocket stomp
import org.springframework.messaging.simp.SimpMessagingTemplate;
//Enviar mensajes programados
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;


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
  // Se ejecuta cada hora 
 // @Scheduled(fixedRate = 3600000) 
   @Scheduled(fixedRate = 10000) 
  public void enviarRecordatorios() { 
    // Carritos con items agregados hace más de 24 horas
    LocalDateTime limite = LocalDateTime.now().minusHours(24);
    List<CartItem> abandonados = cartRepo.findByAddedAtBefore(limite); 
    for (CartItem item : abandonados) { 
      String mensaje = "⏰ Recordatorio: aún tienes " + item.getProduct().getName() + " en tu carrito";
      template.convertAndSend("/topic/notificaciones", mensaje); 
    }
  }

}
