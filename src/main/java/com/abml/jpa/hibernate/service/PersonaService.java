/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.abml.jpa.hibernate.service;

/**
 *
 * @author Flash
 */

import com.abml.jpa.hibernate.model.Persona;
import com.abml.jpa.hibernate.repository.PersonaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Flash
 */
@Service
 @Component
@Transactional

public class PersonaService {
    

   //public class  PersonaService {

        @Autowired
        ////en la variable persoRepository se guarda los datos de la class PersonaRepository
        //que es la encargada de comunicarse con la base de datos
    private PersonaRepository persoRepository;




      
    
   //@Override //(anular o invalidar)
   //findAll es tipo de salida List Array se muestra con *ng for
    public List<Persona> getPersonas(){
        List<Persona> listaPersonas= persoRepository.findAll();
        return listaPersonas;
            
        
    }

    




     //@Override
    //se recibe la variable con datos JSON
    //y a traves de nuestro reposiroty de JPA se hace la persistencia (o almacenamiento)de esos datos a la base de datos
//
        public  void savePersona(Persona perso){
            persoRepository.save(perso);
        }
        
        //para agregar  datos a un registro si no hay datos en la base de datos
          public  void agregarSavePersona(Persona perso){
            persoRepository.save(perso);
        }
        
        
      
       // @Override

        public  void deletePersona(Long dni){
            //persoRepository.deleteById(dni);
            persoRepository.deleteBydni(dni);
        
              
        }
         //@Override

           public Persona findPersona(Long dni){
               //aqui si no oencuentro la persona,devuelvo null por eso va el .orElse
               //el metodo findBynombrecampo de JPARepository no es ArrayList,entonces aqui 
               //definimos nuestra function de tipo Persona
               //nos va a retornar todos los campos del dni solicitado
               //y podemos mostrar sin ngFor
              
              Persona perso= persoRepository.findBydni(dni);
              
            return perso;
        }
           
        
           
       
           
           //@Override
   
   //LOGIN
       //SE RECIBEN LOS DATOS DEL CONTROLLER DEL FORM
   public boolean buscarPorNombre(String paramnombre,String parampassword){
       //buscar como funciona exists,equal,findBy de jpaRepository
       boolean listaPersonas= persoRepository.existsByNombreAndPassword(paramnombre,parampassword);

        return listaPersonas;
       }


   
}
     
   

    



                 
 
        
   

     
   

    



                 
 
        
    
    
    
        
