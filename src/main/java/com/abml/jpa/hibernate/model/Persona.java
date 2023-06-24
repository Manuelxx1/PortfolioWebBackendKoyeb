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
   //@Table(name = "Persona")
public class Persona   {
    @Id //correspondiente al ID y la clave principal en la base de datos
    @GeneratedValue (strategy=GenerationType.SEQUENCE)

    public Long id;
     public Long dni;

    public String nombre;
    public String apellido;
    public String email;
      public String password;
    public int edad;
          public String informacion;
                public String experiencia;
                public String educacion;
                
    
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
   
         
}
