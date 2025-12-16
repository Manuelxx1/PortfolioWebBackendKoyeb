package com.abml.jpa.hibernate.dto;

public class UsersDTO {

    private Long id;           // referencia estable
    private String username;
    private String newUsername;  
    private String password;
    private String email;
    private Long mpUserId;
    private String name;
    private java.sql.Timestamp createdAt;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNewUsername() { return newUsername; }
    public void setNewUsername(String newUsername) { this.newUsername = newUsername; }


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getMpUserId() { return mpUserId; }
    public void setMpUserId(Long mpUserId) { this.mpUserId = mpUserId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
}
