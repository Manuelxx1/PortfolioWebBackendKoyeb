package com.eshop.news.controller;

import com.eshop.news.model.ArticleIndex;
import com.eshop.news.service.ArticleIndexService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/noticias")
@CrossOrigin(origins = "*") // Ajustalo después con la URL específica de tu Angular si es necesario
public class ArticleIndexController {

    private final ArticleIndexService service;

    public ArticleIndexController(ArticleIndexService service) {
        this.service = service;
    }

    // GET http://localhost:8080/api/noticias/buscar?q=phishing
    @GetMapping("/buscar")
    public ResponseEntity<List<ArticleIndex>> buscar(@RequestParam("q") String termino) {
        List<ArticleIndex> resultados = service.buscarArticulos(termino);
        return ResponseEntity.ok(resultados);
    }
}
