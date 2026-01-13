package com.abml.jpa.hibernate.repository;

import com.abml.jpa.hibernate.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {
//Agrega un método custom
    // Buscar orden por preferenceId
    Optional<Orders> findByPreferenceId(String preferenceId);
    List<Orders> findByLoginUsername(String loginUsername);
// NUEVO: buscar orders por externalReference para compra  exitosa 
    Optional<Orders> findByExternalReference(String externalReference);

//ver orders de usuarios en session login
    //@GetMapping("orders/byLogin/{idUsuario}")
    List<Orders> findByUserId(Long userId);
    

    // Alternativa: si Orders tiene una relación ManyToOne con Users
   // List<Orders> findByUser(Users user);
}
