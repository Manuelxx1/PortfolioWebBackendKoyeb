package com.ejercicioabml.abmlcontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
    @EnableScheduling
@EnableJpaRepositories(basePackages = "com.abml.jpa.hibernate.repository")
@EntityScan(basePackages = "com.abml.jpa.hibernate.model")
@ComponentScan(basePackages = {
    "com.abml.jpa.hibernate.service",
    "com.abml.jpa.hibernate.model",
    "com.ejercicioabml.abmlcontroller",
    "com.abml.jpa.hibernate.repository",
    "com.abml.jpa.hibernate.scheduler"
    
})
public class AbmlApplication {

@PostConstruct
    public void init() {
        // Forzamos a la app a usar el huso horario de Argentina (UTC-3)
        //hacemos esto para convertir UTC+3 que se crea por defecto en Timestamp en mysql 
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
    }
    
    public static void main(String[] args) {
        SpringApplication.run(AbmlApplication.class, args);
    }
}
