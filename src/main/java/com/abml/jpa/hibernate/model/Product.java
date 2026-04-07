
package com.abml.jpa.hibernate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;


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

  private String category;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  
  @Enumerated(EnumType.STRING)
  @Column(name = "section")
  private Section section;
  
  // Getters
  public Long getId() { return id; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public BigDecimal getPrice() { return price; }
  public Integer getStock() { return stock; }
  public void setStock(Integer stock) {
    this.stock = stock;
  }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public String getImageUrl() { return imageUrl; }
  public String getCategory() { return category; }

  // Setters (opcional, pero útil si vas a modificar desde el servicio)
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
  
public void setCategory(String category) { this.category = category; }
public void setSection(Section section) { this.section = section; }
}
