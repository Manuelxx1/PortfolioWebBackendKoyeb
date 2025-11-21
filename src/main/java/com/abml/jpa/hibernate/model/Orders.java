
package com.abml.jpa.hibernate.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    //user_id columna en tabla orders wue se usa 
    //como foreign key
    //hacia el id de la tabla users
    //para generar la relación y poder usar
    //sus metodos desde orders
    @JoinColumn(name = "user_id", nullable = false)
 
    //se genera la relación con la class 
    //o entidad Users
    
    private Users user;

    private java.math.BigDecimal total;
    private java.math.BigDecimal amount;

    @Column(length = 20)
    private String status;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Column(name = "created_at", updatable = false, insertable = false)
    private java.sql.Timestamp createdAt;
    
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    //métodos de users que se utilza desde orders
    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }
}
