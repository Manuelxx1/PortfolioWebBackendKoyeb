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
  /* Como en tu base de datos tenés el registro de las palabras clave
  guardado como un único texto largo (por ejemplo: 'ciberseguridad phishing ia estafa'),
  la consulta SELECT DISTINCT keywords te va a devolver ese bloque de texto entero.
  esto sirve pero no queda muy limpio*/
    /* public List<String> obtenerListaDeKeywords(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // Llama a la query DISTINCT que retorna solo la lista de Strings
        return repository.buscarSugerenciasKeyword(termino.trim());
    }
*/

    // Opcional: Si querés separar las palabras por espacios para que Angular reciba términos individuales limpios
    /*si preferís que en la cajita flotante de Angular aparezcan las palabras sueltas e individuales
    (ej: que sugiera sólo "ciberseguridad", luego "phishing", etc.), 
    podés procesar el texto rápidamente en este servicio 
    antes de mandarlo a Angular usando un mapeo básico de Java:  */
public List<String> obtenerListaDeKeywords(String termino) {
    if (termino == null || termino.trim().isEmpty()) {
        return Collections.emptyList();
    }
    
    List<String> filas = repository.buscarSugerenciasKeyword(termino.trim());
    
    // Convertimos las frases largas en palabras individuales 
    //y filtramos las que coincidan con lo que tipeó el usuario
    return filas.stream()
            .flatMap(frase -> java.util.Arrays.stream(frase.split(" "))) // Separa por espacios
            .distinct() // Elimina duplicados
            .filter(palabra -> palabra.toLowerCase().startsWith(termino.toLowerCase().trim())) // Filtra por el inicio
            .limit(5) // Clava el límite en 5 sugerencias
            .toList();
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

    //para obtener los datos para los artículos html
    //usando sus id para que retorne únicamente 
    //los datos del artículo solicitado 
    // Cambiamos List<ArticleIndex> por ArticleIndex
public ArticleIndex buscarPorId(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Artículo no encontrado con el ID: " + id));
}
}
