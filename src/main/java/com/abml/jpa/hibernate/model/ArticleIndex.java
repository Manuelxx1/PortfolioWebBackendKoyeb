//package com.noticias.model;

package com.abml.jpa.hibernate.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "article_search_index")
@Data // Si usás Lombok, si no, generá los Getters y Setters a mano
public class ArticleIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "lead_text", length = 500)
    private String leadText;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String keywords;

    @Column(name = "angular_route", nullable = false)
    private String angularRoute;

    private String category;

    @Column(name = "image_url")
    private String imageUrl;


    

// Este campo es solo para el nacimiento de la nota. Nunca se toca en los UPDATES.
//insertable=false impide que hibernate realice un insert in to desde el frontend 
    //updatable=false impide que hibernate actualize el registro desde el frontend
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
private LocalDateTime fechaCreacion;

// Este campo cambia cada vez que se edita el registro.
@Column(name = "fecha_actualizacion", insertable = false, updatable = false)
private LocalDateTime fechaActualizacion;
    
}
