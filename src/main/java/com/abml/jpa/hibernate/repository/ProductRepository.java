package com.abml.jpa.hibernate.repository;


import com.abml.jpa.hibernate.model.Product;
import com.abml.jpa.hibernate.model.Section;
import java.util.List;
import java.util.Optional;
//class JpaRepository (que maneja repositorios JPA
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


//hacemos un mapping con @Repository
@Repository 
@Component
public interface ProductRepository extends JpaRepository<Product, Long> {
  // Con JpaRepository ya tenés todos los métodos básicos:
    // findById, findAll, save, deleteById, etc.

  //método personalizado 
  List<Product> findByNameContainingIgnoreCase(String name);


  //Buscar los productos por su section buscando en el campo section
    List<Product> findBySection(Section section);


}
