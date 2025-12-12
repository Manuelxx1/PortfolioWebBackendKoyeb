package com.abml.jpa.hibernate.dto;

public class UpdateUsernameRequest {
    private String usuario;
    private String nuevoUsername;

    // getters y setters
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNuevoUsername() {
        return nuevoUsername;
    }
    public void setNuevoUsername(String nuevoUsername) {
        this.nuevoUsername = nuevoUsername;
    }
}
