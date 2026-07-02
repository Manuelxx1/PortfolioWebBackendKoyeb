package com.ejercicioabml.abmlcontroller;

import com.abml.jpa.hibernate.model.ArticleIndex;
import com.abml.jpa.hibernate.service.ArticleIndexService;
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

    //por id para los artículos html para traerlos únicamente los datos 
//del artículo seleccionado 

@GetMapping("/buscarporid")
// Cambiamos List<ArticleIndex> por ArticleIndex en el ResponseEntity
public ResponseEntity<ArticleIndex> buscarporid(@RequestParam("q") Long termino) {
    ArticleIndex resultadoporid = service.buscarPorId(termino);
    return ResponseEntity.ok(resultadoporid);
}

    
    // GET http://localhost:8080/api/noticias/sugerencias?q=ciber
@GetMapping("/sugerencias")
public ResponseEntity<List<String>> obtenerSugerencias(@RequestParam("q") String termino) {
    // Le pide al servicio (y este al repositorio con la query DISTINCT) las palabras clave
    List<String> palabrasSugeridas = service.obtenerListaDeKeywords(termino); 
    return ResponseEntity.ok(palabrasSugeridas);
}


    
//para obtener las noticias de hoy para seccion inicio
    @GetMapping("/hoy")
public ResponseEntity<List<ArticleIndex>> obtenerNoticiasDeHoy() {
    List<ArticleIndex> noticiasDeHoy = service.obtenerNoticiasDeHoy(); // Cambiás por el nombre de tu service
    
    if (noticiasDeHoy.isEmpty()) {
        // Opcional: Podés devolver un 204 No Content si hoy todavía no se publicó nada
        return ResponseEntity.noContent().build(); 
    }
    
    return ResponseEntity.ok(noticiasDeHoy);
}

    
}
