package com.ejercicioabml.abmlcontroller;

import com.abml.jpa.hibernate.model.Comentario;
import com.abml.jpa.hibernate.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
@CrossOrigin(origins = "http://localhost:4200") // Para que conecte con tu Angular local
public class ComentarioController {

    @Autowired
    private ComentarioRepository comentarioRepository;

    // 1. Obtener comentarios de una noticia específica: GET /api/comentarios/noticia/5
    @Override
    @GetMapping("/noticia/{noticiaId}")
    public List<Comentario> obtenerComentariosPorNoticia(@PathVariable Long noticiaId) {
        return comentarioRepository.findByNoticiaIdOrderByFechaDesc(noticiaId);
    }

    // 2. Guardar un nuevo comentario: POST /api/comentarios
    @PostMapping
    public Comentario guardarComentario(@RequestBody Comentario comentario) {
        return comentarioRepository.save(comentario);
    }
}
