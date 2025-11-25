package com.abml.jpa.hibernate.repository;

import com.abml.jpa.hibernate.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    // Buscar orden por preferenceId
    Optional<Orders> findByPreferenceId(String preferenceId);
}
