
package com.abml.jpa.hibernate.service;
import com.abml.jpa.hibernate.repository.ProductRepository;
import com.abml.jpa.hibernate.repository.CartItemRepository;
import com.abml.jpa.hibernate.model.CartItem;
import com.abml.jpa.hibernate.model.Users;
import com.abml.jpa.hibernate.model.Product;

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

  public void addToCart(Users user, Long productId, int quantity) {
    Product product = productRepo.findById(productId)
      .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    CartItem item = new CartItem();
    item.setUser(user);
    item.setProduct(product);
    item.setQuantity(quantity);
    item.setAddedAt(LocalDateTime.now());

    cartRepo.save(item);
  }

  public void removeFromCart(Users user, Long cartItemId) {
    CartItem item = cartRepo.findById(cartItemId)
      .orElseThrow(() -> new RuntimeException("Item no encontrado"));

    if (!item.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("No autorizado");
    }

    cartRepo.delete(item);
  }
}
