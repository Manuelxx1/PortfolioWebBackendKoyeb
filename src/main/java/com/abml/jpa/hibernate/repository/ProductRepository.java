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

  //método personalizado para el buscador principal 
  //List<Product> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String category);

//  Si lo que querés es filtrar por el nombre de la categoría,
  //tenés que apuntar al campo name dentro de Category:
//  Category_Name le dice a Spring Data que use la propiedad name de la entidad relacionada Category.

//El segundo parámetro ahora representa el nombre de la categoría, no el objeto completo.
  List<Product> findByNameContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(String name, String categoryName);

  //Buscar los productos por su section buscando en el campo section
  //section se movió a una tabla nueva por normalizacion de la base
  //a la tabla llamada sections 
  //Revisar este metodo o modificarlo para que funcione otra vez
  List<Product> findBySection(Section section);
//mostrar Productos por categoría 
//Si querés filtrar por el nombre de la categoría (que es un String dentro de la entidad Category)
  // Esto le dice a Spring Data JPA que use la propiedad name de la entidad relacionada Category.
  List<Product> findByCategory_Name(String categoryName);

  //para el backoffice filtro para realizar consultas dinamicas 
@Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category.name = :category) AND " +
           "(:minStock IS NULL OR p.stock > :minStock) AND " +
           "(:maxPrice IS NULL OR p.price < :maxPrice)")
    List<Product> findByFilters(
        @Param("category") String category,
        @Param("minStock") Integer minStock,
        @Param("maxPrice") Double maxPrice
    );

}
