package com.abml.jpa.hibernate.service;
import com.abml.jpa.hibernate.model.Product;
import com.abml.jpa.hibernate.model.Section;
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

  //traer todos los productos 
public List<Product> findAll() {
        return productRepo.findAll();
}
//Buscar productos o datos por section
  public List<Product> getFeaturedProducts() {
        return productRepo.findBySection(Section.DESTACADOS);
  }
  
 
  @Autowired
    private PasswordEncoder passwordEncoder;
public void updatePassword(String usuario, String nuevaPassword) {
        Users user = userRepository.findByUsername(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(nuevaPassword));// acá deberías encriptar la contraseña
        userRepository.save(user);
}

  public Users updateUsername(Long id, String nuevoUsername) {
    Users user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // Validar que no exista otro con el mismo nuevo username
    if (userRepository.findByUsername(nuevoUsername).isPresent()) {
        throw new RuntimeException("El nuevo username ya está en uso");
    }

    user.setUsername(nuevoUsername);
   return userRepository.save(user);
}


  
  public Users updateEmail(Long id, String nuevoEmail) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email no encontrado"));
    if (userRepository.findByEmail(nuevoEmail).isPresent()) {
        throw new RuntimeException("El nuevo email ya está en uso");
    }

        user.setEmail(nuevoEmail);
       return userRepository.save(user);
  }
  
}
