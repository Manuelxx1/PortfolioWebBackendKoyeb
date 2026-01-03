


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



/**
 *
 * @author Flash
 */
//hacemos un mapping con @Repository
@Repository 
@Component
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  List<CartItem> findByUser(Users user);


  // Busca carritos con items agregados antes de cierta fecha 
//findByAddedAt es el campo en tabla Before es el método 
  List<CartItem> findByAddedAtBefore(LocalDateTime limite);

}
