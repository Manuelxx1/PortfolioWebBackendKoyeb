package com.eshop.news.service;

import com.eshop.news.model.ArticleIndex;
import com.eshop.news.repository.ArticleIndexRepository;
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

    public List<ArticleIndex> buscarArticulos(String termino) {
        // Validación simple para evitar pegarle a la BD si el buscador está vacío
        if (termino == null || termino.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return repository.buscarPorPalabrasClave(termino.trim());
    }
}
