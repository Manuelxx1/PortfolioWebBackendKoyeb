
package com.abml.jpa.hibernate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

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
}
