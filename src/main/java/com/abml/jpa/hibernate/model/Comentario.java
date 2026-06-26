package com.abml.jpa.hibernate.model; // Ajustá el package según tu estructura actual

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String autor;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    private LocalDateTime fecha;

    // El identificador de la noticia a la que pertenece el comentario
    private Long noticiaId; 

    public Comentario() {
    }

    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Long getNoticiaId() { return noticiaId; }
    public void setNoticiaId(Long noticiaId) { this.noticiaId = noticiaId; }
}
