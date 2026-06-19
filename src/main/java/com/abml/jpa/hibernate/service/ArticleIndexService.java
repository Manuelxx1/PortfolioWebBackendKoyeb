package com.abml.jpa.hibernate.service;

import com.abml.jpa.hibernate.model.ArticleIndex;
import com.abml.jpa.hibernate.repository.ArticleIndexRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ArticleIndexService {

    private final ArticleIndexRepository repository;

    
    // Inyección por constructor (Práctica recomendada en lugar de @Autowired)
    public ArticleIndexService(ArticleIndexRepository repository) {
        this.repository = repository;
    }

// =========================================================================
    // 1. EL MÉTODO QUE TE FALTABA: Para las sugerencias rápidas (Modo Google)
    // =========================================================================
    public List<String> obtenerListaDeKeywords(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // Llama a la query DISTINCT que retorna solo la lista de Strings
        return repository.buscarSugerenciasKeyword(termino.trim());
    }


    
    // =========================================================================
    // 2. PARA LA BÚSQUEDA FINAL: Retorna las tarjetas completas con sus rutas
    // =========================================================================
    public List<ArticleIndex> buscarArticulos(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // Llama a la query SELECT * que mapea los objetos ArticleIndex completos
        return repository.buscarPorPalabrasClaveCompleto(termino.trim());
    }

    
}
