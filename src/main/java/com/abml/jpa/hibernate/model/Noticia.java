
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.abml.jpa.hibernate.model;

/**
 *
 * @author Flash
 */

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;






/**
 *
 * @author Flash
 */

//los @setteres son fundamentales para recibir los datos desde el controller hacia esta class 
//ylos @getters para obtner los datos en el controller y enviarlo hacia el Front End en este caso 
//hacia angular
//
@Getter @Setter //getters y setter automatizados,no hace falta crearlos
@Entity //@Entity: indica que esta es una entidad con la que se va a trabajar para hacer la persistencia hacia la base de datos
   //@Table(name = "Noticia")
@Entity
public class Noticia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String informacion;

    private String linkHtml;
}
