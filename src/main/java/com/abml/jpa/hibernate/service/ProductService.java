package com.abml.jpa.hibernate.service;
import com.abml.jpa.hibernate.model.Product;
import com.abml.jpa.hibernate.model.Section;
import com.abml.jpa.hibernate.repository.ProductRepository;
import com.abml.jpa.hibernate.repository.UserRepository;
import com.abml.jpa.hibernate.repository.SectionRepository;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;
import com.abml.jpa.hibernate.model.Users;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class ProductService {

  @Autowired private ProductRepository productRepo;
    @Autowired private UserRepository userRepository;
@Autowired
    private SectionRepository sectionRepo;
  
  //para el buscador principal 
  public List<Product> searchByNameOrCategory(String name) {
   // return productRepo.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(name,name); 
    return productRepo.findByNameContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(name,name);
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
  //usando los datos del archivo  enum section 
 //enum ya no se usa más porque section ahora es una entidad propia 
  //no pertenece más como campo de la tabla products
// ahora es una tabla llamada sections que se creo por normalizacion de la base
  //como parámetro de consulta
     /* public List<Product> getFeaturedProducts() {
    return productRepo.findBySection_NameIgnoreCase("Destacados");

      }
  */

  public List<Product> getFeaturedProducts() {
    return productRepo.findFeaturedProducts();
}


public List<Product> getProductsEnOferta() {
    //return productRepo.findBySection_NameIgnoreCase("Ofertas");
return productRepo.findFeaturedProducts();
}


  //productos por categoría 
  
 public List<Product> getProductsByCategory(String category) {
        return productRepo.findByCategory_NameIgnoreCase(category);
   
 }
  
  //endpoints para el dashboard de usuario
  //para cambiar contraseña,username,email y alguna otra cosa 
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


  //para cambiar contraseña por solucitud de usuario 
  //desde el login por no recordar contraseña

  //este es el service que es el que se comunica con Termux 
  //usando una URL túnel que conecta a localhost del dispositivo que ejecuta Termux 
  //esto es porque render u otro no deja usar smtp en el modo gratis
  //para evitar qye se haga spam con sus servidores 
  //si en el modo pago deja usar smtp entonces
//ya no hace falta usar Termux 
  //y la lógica del envío de email que esta en Termux se usaría en un servicio en render 
  public void savePasswordResetToken(String email, String passwordResetToken) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email no encontrado"));

        user.setPasswordResetToken(passwordResetToken);
        userRepository.save(user);
//restTemplate nos permite conectarnos a termux en el dispositivo localhost 
    RestTemplate restTemplate = new RestTemplate();
  //url túnel para conectarnos a Termux en dispositivo localhost 
    String urlBase = System.getenv("TERMUX_URL");
    //url tunel con endpoint que está en Termux 
     String termuxEmailApi = urlBase + "/api/send-token-recuperar-contraseña";

  Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("token", passwordResetToken);
      //se envia a Termux usando restTemplate 
    restTemplate.postForObject(termuxEmailApi, body, String.class);
  }


  
  
  public void resetPassword(String token, String newPassword) {
        Users user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        // Encriptar la nueva contraseña con BCrypt
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        // Limpiar el token para que no se reutilice
        user.setPasswordResetToken(null);

        userRepository.save(user);
  }

  //para cambiar usuario desde el login


  public void saveUsernameResetToken(String email, String usernameResetToken) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email no encontrado"));

        user.setUsernameResetToken(usernameResetToken);
        userRepository.save(user);
//restTemplate nos permite conectarnos a termux en el dispositivo localhost 
    RestTemplate restTemplate = new RestTemplate();
  //url túnel para conectarnos a Termux en dispositivo localhost 
    String urlBase = System.getenv("TERMUX_URL");
    //url tunel con endpoint que está en Termux 
     String termuxEmailApi = urlBase + "/api/send-token-recuperar-usuario";

  Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("token", usernameResetToken);
      //se envia a Termux usando restTemplate 
    restTemplate.postForObject(termuxEmailApi, body, String.class);
  }

  public void resetUsername(String token, String newUsername ) {
        Users user = userRepository.findByUsernameResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        user.setUsername(newUsername);

        // Limpiar el token para que no se reutilice
        user.setUsernameResetToken(null);

        userRepository.save(user);
  }

//para gestión de productos backoffice 
  //para el método read(get) se usa el método findAll que está
//al principio de este archivo 
  
  //create
  public Product createProduct(Product product) {
        return productRepo.save(product);

    
  }

  // Método de actualización
    public Product updateProduct(Long id, Product product) {
        Product existing = productRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        existing.setDescription(product.getDescription());
      existing.setImageUrl(product.getImageUrl());
existing.setSection(product.getSection());
        existing.setCategory(product.getCategory());
      return productRepo.save(existing);
    }


  //delete
  public void deleteProduct(Long id) {
        productRepo.deleteById(id);
  }

  

}
