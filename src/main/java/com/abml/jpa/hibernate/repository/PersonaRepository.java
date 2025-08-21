/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.abml.jpa.hibernate.repository;

/**
 *
 * @author Flash
 */

import com.abml.jpa.hibernate.model.Persona;
import java.util.List;
import java.util.Optional;
//class JpaRepository (que maneja repositorios JPA
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;



/**
 *
 * @author Flash
 */
//hacemos un mapping con @Repository
@Repository 
@Component
//la interface extiende o hereda la class JpaRepository (que maneja repositorios JPA
//en los parametros <> deben ir: clase a persistir o transformar que sera la tabla a la que JPA genera consultas
//y tipo de dato de su Id que se usa como primary key
//si quiero usar otro tipo de dato numerico tipo int en la class Persona y en el controller y el service
//debo cambiar aqui tambien cambiano a Long por int 
public interface PersonaRepository extends JpaRepository <Persona,Long>,JpaSpecificationExecutor<Persona>{
 
    //@Query(value = "select * from book b where b.name=?1", nativeQuery = true)
    //@Query(value ="select l.id,l.username,l.userpassword from Login l where l.username=:name and l.userpassword=:password")
   // iniciar sesión
 // @Query(value = "select * from Persona  where nombre=?1",nativeQuery = true)
  // List findBynombre(String n);
 
 
         // registrarse
  // @Query(value = "select * from registrologin  where username=?1", nativeQuery = true)
 
         //List<Persona> findByUsername(String username);
         
void deleteBydni(Long dni);
   Persona findBydni (Long dni);
 
   

 
  //boolean findByNombre(String nombre);
      boolean existsByNombreAndPassword(String paramnombre,String parampassword);     

    
   List<Persona> findByInformacionContainingIgnoreCase(String frase);

}

