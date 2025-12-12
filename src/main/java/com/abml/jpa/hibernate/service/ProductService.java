package com.abml.jpa.hibernate.service;
import com.abml.jpa.hibernate.model.Product;
import com.abml.jpa.hibernate.repository.ProductRepository;
import com.abml.jpa.hibernate.repository.UserRepository;
import com.abml.jpa.hibernate.model.Users;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class ProductService {

  @Autowired private ProductRepository productRepo;
    @Autowired private UserRepository userRepository;

  public List<Product> searchByName(String name) {
    return productRepo.findByNameContainingIgnoreCase(name);
}


  public Product getProductById(Long id) {
    return productRepo.findById(id)
      .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
  }

 
  @Autowired
    private PasswordEncoder passwordEncoder;
public void updatePassword(String usuario, String nuevaPassword) {
        Users user = userRepository.findByUsername(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(nuevaPassword));// acá deberías encriptar la contraseña
        userRepository.save(user);
}

  public void updateUsername(String usuario, String nuevoUsername) {
        Users user = userRepository.findByUsername(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setNuevoUsername(nuevoUsername);// acá deberías encriptar la contraseña
        userRepository.save(user);
  }
  
}
