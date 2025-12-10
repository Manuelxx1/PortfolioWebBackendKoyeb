package com.abml.jpa.hibernate.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import com.fasterxml.jackson.annotation.JsonIgnore;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

  
    @Column(nullable = false, length = 100)
   @JsonIgnore   //  esto evita que se muestre el campo en el JSON
    //cuando se accede al endpoint por url en navegador
    //y también por frontend en vista ya que es un dato privado del sistema 
    private String password;

    @Column(length = 100)
    private String email;

    @Column(name = "mp_user_id", unique = true)
    private Long mpUserId;

    @Column(length = 255)
    private String name;

    @Column(name = "created_at", updatable = false, insertable = false)
    private java.sql.Timestamp createdAt;

    // Relación inversa opcional (solo si querés navegar desde Users hacia Orders)
   // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<Orders> orders;
    
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getMpUserId() { return mpUserId; }
    public void setMpUserId(Long mpUserId) { this.mpUserId = mpUserId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
public java.sql.Timestamp getcreatedAt() { return createdAt; }
   // public List<Orders> getOrders() { return orders; }
   // public void setOrders(List<Orders> orders) { this.orders = orders; }
  }
