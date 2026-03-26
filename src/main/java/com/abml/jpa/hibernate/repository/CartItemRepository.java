



package com.abml.jpa.hibernate.repository;

/**
 *
 * @author Flash
 */

import com.abml.jpa.hibernate.model.CartItem;
import com.abml.jpa.hibernate.model.Users;
import java.util.List;
import java.util.Optional;
//class JpaRepository (que maneja repositorios JPA
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 *
 * @author Flash
 */
//hacemos un mapping con @Repository
@Repository 
@Component
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  List<CartItem> findByUser(Users user);
  List<CartItem> findById(Users user);
  
  // Este método busca un ítem que coincida con el usuario Y el producto
  //y así poder agregar items al carrito aumentando la cantidad nomas
  //sin que se repita el registro del items en la tabla
  Optional<CartItem> findByUserAndProductId(Users user, Long productId);


  // Busca carritos con items agregados antes de cierta fecha 
//findByAddedAt es el campo en tabla y Before es el método 
  //Con esto, Spring Data entiende que querés todos los CartItem cuya fecha addedAt sea anterior al parámetro limite.
  //este método es para las notificaciones del WebSocket 
  List<CartItem> findByAddedAtBefore(LocalDateTime limite);

}
