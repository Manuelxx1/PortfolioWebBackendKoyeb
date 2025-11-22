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
@Table(name = "order_items")
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Orders: cada ítem pertenece a una orden
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    // Relación con Products: cada ítem referencia un producto
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

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

    public Products getProduct() { return product; }
    public void setProduct(Products product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}
