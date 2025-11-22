package com.abml.jpa.hibernate.model;
import jakarta.persistence.Entity;
import  com.abml.jpa.hibernate.model.Product;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;


// Para romper ciclos en JSON
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "order_items")
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Orders
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
  //para evitar el loop de datos de json por repercusión de datos en la relación 
    //cuando se accede al endpoint orders
    @JsonBackReference
    private Orders order;

    // Relación con Product (tu clase existente)
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 38, scale = 2)
    private BigDecimal amount;

    @Column(name = "product_name", length = 255)
    private String productName;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Orders getOrder() { return order; }
    public void setOrder(Orders order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
        }
