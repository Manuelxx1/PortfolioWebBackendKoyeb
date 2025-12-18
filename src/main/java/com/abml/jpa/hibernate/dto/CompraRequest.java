
package com.abml.jpa.hibernate.dto;

// CompraRequest.java
//este dto reemplaza a Map<String, Object> 
//en el endpoint create del checkout mp para tener mejor control y acceso
//de los datos que llegan al endpoint 
public class CompraRequest {
    private int quantity;
    private Long idUsuario; // <-- nuevo campo
    private String usuario;

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



        @Override
    public String toString() {
        return "CompraRequest{" +
                "quantity=" + quantity +
                ", idUsuario'" + idUsuario + '\'' +
                '}';
    }

}
