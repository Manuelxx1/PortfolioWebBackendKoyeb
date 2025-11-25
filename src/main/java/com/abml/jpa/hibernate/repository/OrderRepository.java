package com.abml.jpa.hibernate.repository;

import com.abml.jpa.hibernate.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {
//Agrega un método custom
    // Buscar orden por preferenceId
    Optional<Orders> findByPreferenceId(String preferenceId);
}
