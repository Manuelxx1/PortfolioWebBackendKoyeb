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
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // este es el ID interno de tu sistema

    private String name;
    private String email;

    @Column(name = "mp_user_id", unique = true)
    private Long mpUserId; //  ID del usuario en Mercado Pago


    
    //  Nuevo campo para mapear la columna 'username' de la tabla
    private String username;

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

   // public List<Orders> getOrders() { return orders; }
   // public void setOrders(List<Orders> orders) { this.orders = orders; }
  }
