package com.abml.jpa.hibernate.dto;

public class UpdatePasswordRequest {
    private String usuario;
    private String nuevaPassword;

    // getters y setters
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }
    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }
}
