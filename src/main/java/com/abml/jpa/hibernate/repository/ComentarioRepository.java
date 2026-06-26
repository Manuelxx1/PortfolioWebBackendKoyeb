package com.abml.jpa.hibernate.repository;

import com.abml.jpa.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    
    // Trae los comentarios de una noticia específica ordenados por fecha descendente
    List<Comentario> findByNoticiaIdOrderByFechaDesc(Long noticiaId);
}
