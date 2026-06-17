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
    @Query(value = "SELECT * FROM article_search_index WHERE MATCH(title, lead_text, keywords) AGAINST(:termino IN NATURAL LANGUAGE MODE)", nativeQuery = true)
    List<ArticleIndex> buscarPorPalabrasClave(@Param("termino") String termino);
}
