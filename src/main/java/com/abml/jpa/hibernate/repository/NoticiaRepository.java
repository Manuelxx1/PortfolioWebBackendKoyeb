package com.abml.jpa.hibernate.repository;

/**
 *
 * @author Flash
 */

import com.abml.jpa.hibernate.model.Noticia;
import java.util.List;
import java.util.Optional;
//class JpaRepository (que maneja repositorios JPA
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;



/**
 *
 * @author Flash
 */
//hacemos un mapping con @Repository
@Repository 
@Component




public interface NoticiaRepository extends JpaRepository<Noticia, Long> {
    List<Noticia> findByInformacionContainingIgnoreCase(String keyword);
}
