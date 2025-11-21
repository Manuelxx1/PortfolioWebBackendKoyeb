
package com.abml.jpa.hibernate.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private BigDecimal amount;
    private String status;

    @Column(name = "total")
    private BigDecimal total; // nuevo campo obligatorio en la tabla

    @ManyToOne
    //user_id columna en tabla orders wue se usa 
    //como foreign key
    //hacia el id de la tabla users
    //para generar la relación y poder usar
    //sus metodos desde orders
    @JoinColumn(name = "user_id") 
    //se genera la relación con la class 
    //o entidad Users
    private Users user;

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
