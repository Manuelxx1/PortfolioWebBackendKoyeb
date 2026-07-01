//package com.noticias.model;

package com.abml.jpa.hibernate.model;


import jakarta.persistence.*;
import lombok.Data;

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


    private LocalDateTime fecha;


    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();
    }
}
