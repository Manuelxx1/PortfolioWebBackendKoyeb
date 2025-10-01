/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 
package com.abml.jpa.hibernate.model;

/**
 *
 * @author Flash
 */
/*
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
//@Getter @Setter //getters y setter automatizados,no hace falta crearlos
//@Entity //@Entity: indica que esta es una entidad con la que se va a trabajar para hacer la persistencia hacia la base de datos
   //@Table(name = "Persona")
/* public class Persona   {
    @Id //correspondiente al ID y la clave principal en la base de datos
    @GeneratedValue (strategy=GenerationType.IDENTITY)

    private Long id;
     private Long dni;

    private String nombre;
    private String apellido;
    private String email;
      private String password;
    private int edad;
          private String informacion;
                private String experiencia;
                private String educacion;
                
    */
           /* public Persona(int Pid,String Pnombre, String Papellido,int Pedad){
             //cuando obtenemos los datos desde un JSP/HTML o un simulador de solicitudes HTTP REST como el software Postman
             //guardamos en las variables y asi poder acceder a esos valores
             //desde los metodos de esta class o desde otra Class  externa en este caso Controller.java
             

               id =Pid;
   nombre=Pnombre;
   apellido=Papellido;     
      edad=Pedad;  
 
         }
        */     
   
         
//}

package com.abml.jpa.hibernate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dni;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private int edad;
    private String informacion;
    private String experiencia;
    private String educacion;

    // Getters
    public Long getId() {
        return id;
    }

    public Long getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getEdad() {
        return edad;
    }

    public String getInformacion() {
        return informacion;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public String getEducacion() {
        return educacion;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public void setEducacion(String educacion) {
        this.educacion = educacion;
    }
}

