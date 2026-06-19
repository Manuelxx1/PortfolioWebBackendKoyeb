package com.abml.jpa.hibernate.repository;
import com.abml.jpa.hibernate.model.ArticleIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleIndexRepository extends JpaRepository<ArticleIndex, Long> {

    // Consulta nativa para aprovechar el índice FULLTEXT de MySQL
  /*  @Query(value = "SELECT * FROM article_search_index WHERE MATCH(title, lead_text, keywords) AGAINST(:termino IN NATURAL LANGUAGE MODE)", nativeQuery = true)
    List<ArticleIndex> buscarPorPalabrasClave(@Param("termino") String termino);
*/

    
    //Buscador con sugerencias 
    // =========================================================================
    // 1. PARA LA BÚSQUEDA FINAL (Método: searchArticulos en Angular)
    // Retorna los artículos COMPLETOS mapeados como objetos de la entidad.
    // =========================================================================
    @Query(value = "SELECT * FROM article_search_index WHERE MATCH(title, lead_text, keywords) AGAINST(CONCAT(:termino, '*') IN BOOLEAN MODE)", nativeQuery = true)
    List<ArticleIndex> buscarPorPalabrasClaveCompleto(@Param("termino") String termino);


    // =========================================================================
    // 2. PARA LAS SUGERENCIAS (Método: getSugerenciasKeywords en Angular)
    // Retorna únicamente una lista de TEXTOS planos (Strings) sin más datos.
    // =========================================================================
    @Query(value = "SELECT DISTINCT keywords FROM article_search_index WHERE MATCH(title, lead_text, keywords) AGAINST(CONCAT(:termino, '*') IN BOOLEAN MODE) LIMIT 5", nativeQuery = true)
    List<String> buscarSugerenciasKeyword(@Param("termino") String termino);
}
