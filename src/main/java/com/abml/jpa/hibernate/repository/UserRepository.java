package com.abml.jpa.hibernate.repository;

import com.abml.jpa.hibernate.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    // método extra para buscar por mpUserId
   Optional<Users> findByMpUserId(Long mpUserId);
Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email); //  este faltaba
Optional<Users> findById(Long id);
 


}
