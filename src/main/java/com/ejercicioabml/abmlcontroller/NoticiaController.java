


package com.ejercicioabml.abmlcontroller;



import com.abml.jpa.hibernate.model.Noticia;





import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
//para crear las tables en nuestra base de datos
//es fundamental las siguientes anotations
@EnableJpaRepositories (basePackages = "com.abml.jpa.hibernate.repository") //Escanea el repositorio bajo el paquete especificado

@EntityScan (basePackages = "com.abml.jpa.hibernate.model")//Escanea las entidades bajo el paquete especificado






@RestController
@RequestMapping("/api/noticias")
public class NoticiaController {

    @Autowired
    private NoticiaRepository repository;

    @GetMapping("/buscar")
    public List<Noticia> buscar(@RequestParam String keyword) {
        return repository.findByInformacionContainingIgnoreCase(keyword);
    }
}
