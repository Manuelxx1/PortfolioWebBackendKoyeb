
package com.abml.jpa.hibernate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import com.abml.jpa.hibernate.model.Section;
import com.abml.jpa.hibernate.model.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;
  
  private BigDecimal price;
  private Integer stock;

  @Column(name = "image_url")
  private String imageUrl;

  

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  //al mover la columna section de products
  //a una tabla nueva llamada sections 
  //se debería modificar esto estableciendola relacion
//con esa tabla
  //esto ya no sirve mas
  /*@Enumerated(EnumType.STRING)
  
    @Column(name = "section")
  private Section section;
  */
  
@ManyToOne
//@JoinColumn(name = "section_id", nullable = false)
//temporal lo hacemos  nullable true para probar el form de gestión de productos 
//de forma simple hasta que se agreguen los otros datos de relación desde el form de gestión de 
 //productos 
  @JoinColumn(name = "section_id", nullable = true)
  //  Ignoramos la lista de productos dentro de Section para evitar el loop
    @JsonIgnoreProperties("products")
  private Section section;

//crea la relación con la tabla categories
  //mediante el campo category_id de products
 //category_id es la foreign key de productos
  //al tener la relación no hace falta declarar el campo
  //category_id aparte,Hibernate ya sabe que existe
  //la property en products
  @ManyToOne
   // @JoinColumn(name = "category_id",referencedColumnName = "id" )
   @JoinColumn(name = "category_id", nullable = true)
  private Category category;
  
  // Getters
  public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }
  public Category getCategory() {
    return category;
}

public void setCategory(Category category) {
    this.category = category;
}

}
