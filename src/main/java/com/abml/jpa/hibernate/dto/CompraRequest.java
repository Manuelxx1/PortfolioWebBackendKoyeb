
package com.abml.jpa.hibernate.dto;

// CompraRequest.java
//este dto reemplaza a Map<String, Object> 
//en el endpoint create del checkout mp para tener mejor control y acceso
//de los datos que llegan al endpoint 
public class CompraRequest {
    private int quantity;
    private Long idUsuario; // <-- nuevo campo
    private String usuario;
   // private String shippingType; // "standard", "express", "pickup" 
    //private double shippingCost;
   //private String shippingName;

    // Datos personales 
    private String name;
    private String dni;
    private String email; 
    private String phone;
    private String address;
    private String city; 
    private String postalCode; 
    // Datos de envío 
    private String shippingType; 
    private double shippingCost;
    private String shippingName;


    // Getters y setters
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }



    

    public String getName() { 
        return name; 
    }
    public void setName(String name) {
        this.name = name; 
    } 
   
    public String getDni() { 
        return dni; 
    }
 
    public void setDni(String dni) {
        this.dni = dni; 
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


        @Override
    public String toString() {
        return "CompraRequest{" +
                "quantity=" + quantity +
                ", idUsuario='" + idUsuario + '\'' +
                '}';
    }
}
