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
import jakarta.persistence.FetchType;


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

    // Relación con Users: siempre cargada (EAGER)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // Datos auxiliares del login
    @Column(name = "login_username", length = 100)
    private String loginUsername;

    @Column(name = "login_email", length = 150)
    private String loginEmail;

    // Datos del payer de Mercado Pago
    @Column(name = "mp_payer_name", length = 255)
    private String mpPayerName;

    @Column(name = "mp_payer_email", length = 255)
    private String mpPayerEmail;

    // Campo para vincular con Mercado Pago
    @Column(name = "preference_id", length = 255)
    private String preferenceId;

    @Column(name = "external_reference", length = 255)
    private String externalReference;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
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

    // Datos personales del comprador 
    private String name; 
    private String email; 
    private String phone; 
    private String address; 
    private String city; 
    private String postalCode; 
    // Datos de envío
    private String shippingType;
    private double shippingCost; 
    private String shippingName;
   
    // Método helper para calcular el total dinámicamente
    /*
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
*/
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public String getLoginUsername() { return loginUsername; }
    public void setLoginUsername(String loginUsername) { this.loginUsername = loginUsername; }

    public String getLoginEmail() { return loginEmail; }
    public void setLoginEmail(String loginEmail) { this.loginEmail = loginEmail; }

    public String getMpPayerName() { return mpPayerName; }
    public void setMpPayerName(String mpPayerName) { this.mpPayerName = mpPayerName; }

    public String getMpPayerEmail() { return mpPayerEmail; }
    public void setMpPayerEmail(String mpPayerEmail) { this.mpPayerEmail = mpPayerEmail; }

    public String getPreferenceId() { return preferenceId; }
    public void setPreferenceId(String preferenceId) { this.preferenceId = preferenceId; }

    public String getExternalReference() { return externalReference; }
    public void setExternalReference(String externalReference) { this.externalReference = externalReference; }

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

    public String getName() { 
        return name; 
    }
    public void setName(String name) {
        this.name = name; 
    } 
    public String getEmail() { 
        return email; 
    } 
    public void setEmail(String email) { 
        this.email = email; 
    }
    public String getPhone() { 
        return phone; 
    } 
    public void setPhone(String phone) {
        this.phone = phone; 
    } 
    public String getAddress() { 
        return address;
    }
    public void setAddress(String address) {
        this.address = address; 
    } 
    public String getCity() {
        return city; 
    } 
    public void setCity(String city) { 
        this.city = city; 
    } 
    public String getPostalCode() {
        return postalCode;
    } 
    public void setPostalCode(String postalCode) { 
        this.postalCode = postalCode; 
    }
    public String getShippingType() {
        return shippingType;
    } 
    public void setShippingType(String shippingType) { 
        this.shippingType = shippingType;
    }
    public double getShippingCost() {
        return shippingCost;
    } 
    public void setShippingCost(double shippingCost) { 
        this.shippingCost = shippingCost;
    } 
    public String getShippingName() {
        return shippingName;
    } 
    public void setShippingName(String shippingName) { 
        this.shippingName = shippingName;
    } 

}
