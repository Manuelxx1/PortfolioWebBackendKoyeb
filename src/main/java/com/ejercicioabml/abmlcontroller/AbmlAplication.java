package com.ejercicioabml.abmlcontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.abml.jpa.hibernate.repository")
@EntityScan(basePackages = "com.abml.jpa.hibernate.model")
@ComponentScan(basePackages = {
    "com.abml.jpa.hibernate.service",
    "com.abml.jpa.hibernate.model",
    "com.ejercicioabml.abmlcontroller",
    "com.abml.jpa.hibernate.repository"
})
public class AbmlApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbmlApplication.class, args);
    }
}
