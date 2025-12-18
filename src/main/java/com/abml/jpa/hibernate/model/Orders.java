package com.abml.jpa.hibernate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

// Para romper ciclos en JSON
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;


import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

// Datos del payer de Mercado Pago
    private String mpPayerName;
    private String mpPayerEmail;

    //  Campo para vincular con Mercado Pago
    private String preferenceId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
   //para evitar el loop de datos de json por repercusión de datos en la relación 
    //cuando se accede al endpoint orders
    @JsonManagedReference
    private List<OrderItems> items;

    private BigDecimal total;
    private BigDecimal amount;

    @Column(length = 20)
    private String status;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;

    
    // Método helper para calcular el total dinámicamente
//va a ser llamado por el PaymentController 
    public void calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (items != null) {
            for (OrderItems i : items) {
                total = total.add(i.getAmount());
            }
        }
        this.total = total;
        this.amount = total;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public List<OrderItems> getItems() { return items; }
    public void setItems(List<OrderItems> items) { this.items = items; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getPreferenceId() { return preferenceId; }
    public void setPreferenceId(String preferenceId) { this.preferenceId = preferenceId; }

public String getMpPayerName() { return mpPayerName; }
    public void setMpPayerName(String mpPayerName ) { this.mpPayerName = mpPayerName; }

    public String getMpPayerEmail() { return mpPayerEmail; }
    public void setMpPayerEmail(String mpPayerEmail) { this.mpPayerEmail = mpPayerEmail; }
    
}
