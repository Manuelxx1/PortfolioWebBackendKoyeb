package com.ejercicioabml.abmlcontroller;



import com.abml.jpa.hibernate.model.Persona;


import com.abml.jpa.hibernate.model.Persona;

import com.abml.jpa.hibernate.service.PersonaService;
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

@ComponentScan (basePackages = "com.abml.jpa.hibernate.service", "com.abml.jpa.hibernate.model", "com.ejercicioabml.abmlcontroller.NoticiaController",
"com.abml.jpa.hibernate.repository" ) // Especifica el paquete que se va a escanear, de lo contrario, solo el paquete donde se encuentra esta clase

//luego de haber creado la table ya es posibles realizar el ABML en la base de datos usando los endpoints
@RestController 
//@CrossOrigin afecta el funcionamiento de Postman
//@CrossOrigin es fundamental para conectar angular con el backend Springboot
@CrossOrigin (origins="https://portfoliowebkoyeb.web.app/")




public class AbmlcontrollerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbmlcontrollerApplication.class, args);
	}
        
        
        //con Autowired inyectamos la dependecia que queremos usar sin crear un objeto instancia de esa class
        
        @Autowired

        private 
                //en la variable interPersona se guarda los datos de la class PersonaService
                //que es la encargada de llamar a JPARepositories
                PersonaService interPersona ;
       
        
        //ENDPOINTS
        //cuando accedemos a  la ruta personas/traer
        //se activa getPersonas() dentro de este metodo hay un llamado 
        //a nuestra class dependencia PersonaService que es la encargada 
        //de de generar acciones traves sus metodos usando a  la class PersonaRepository
        //que maneja o hereda a  la JpaRepository
        //endpoints 
        @GetMapping ("personas/traer")
        public List<Persona> getPersonas(){
            return interPersona.getPersonas();
        }
        //por dni 
        //nos sirve para traer los campos por id o dni 
        //que luego en la vista decidimos que campo mostrar
        //para el imput text a editar
        //sin usar un ngfor
           @GetMapping ("personas/traer/{dni}")
        public  Persona  findPersona(@PathVariable Long dni){
             Persona perso= interPersona.findPersona(dni);
             return perso;
        }
    
  
           //endpoints
        //crear el nombre y demas usando la class Persona,esto es para registrarse 
        //en el portfolio con algun nombre usando el boton de Registro en el FrontEnd
        
        @PostMapping ("personas/crear")
        public String createStudent(@RequestBody Persona perso){
            interPersona.savePersona(perso);
            //retorna un String avisando si creo correctamente
            return "La persona fue creada correctamente";
            
        }
        
         //endpoints 
        //editar info Portfolio
        @PutMapping("personas/editarPortfolio/{dni}")
        public Persona editInfoPortfolio (@PathVariable Long dni,@RequestBody String pinformacion){

    //busco a la persona usando nuestra dependencia
  Persona perso= interPersona.findPersona(dni);
  

  //con el id de donde queremos haecr el update
    //le asigno los valores que obtuvimos en el constructor de editPersona
    //a los setters de la class Persona

perso.setInformacion(pinformacion);//tipo de datos de salida tipo String 
    
    
    //usando nuestra dependencia guardo los datos que acabamos de actualizar  en nuestra base de datos
    interPersona.savePersona(perso);
    //retorna la nueva persona
    return perso;
    
           
        }
          //endpoints 
        //agregar info Portfolio
        @PostMapping("personas/agregar/{dni}")

        public Persona agregarInfoPortfolio (@PathVariable Long dni,@RequestBody String pinformacion){

    //busco a la persona usando nuestra dependencia
  Persona perso= interPersona.findPersona(dni);
  
  //con el id de donde queremos agregar datos 
    //le asigno los valores que obtuvimos en el constructor de agregarInfoPortfolio
    //a los setters de la class Persona

perso.setInformacion(pinformacion);//tipo de datos de salida tipo String 
    
    
    //usando nuestra dependencia guardo los datos que acabamos de actualizar  en nuestra base de datos
    interPersona.agregarSavePersona(perso);
    //retorna la nueva persona
    return perso;
    
           
        }
                          //endpoints 
        //agregar Educacion Portfolio
        @PostMapping("personas/agregarPortfolioEducacion/{dni}")

        public Persona agregarEducacionPortfolio (@PathVariable Long dni,@RequestBody String pEducacion){

    //busco a la persona usando nuestra dependencia
  Persona perso= interPersona.findPersona(dni);
  
  //con el id de donde queremos agregar datos 
    //le asigno los valores que obtuvimos en el constructor de agregarInfoPortfolio
    //a los setters de la class Persona

perso.setEducacion(pEducacion);//tipo de datos de salida tipo String 
    
    
    //usando nuestra dependencia guardo los datos que acabamos de actualizar  en nuestra base de datos
    interPersona.agregarSavePersona(perso);
    //retorna la nueva persona
    return perso;
    
           
        }
                 //endpoints 
        //agregar Experiencia Portfolio
        @PostMapping("personas/agregarPortfolioExperiencia/{dni}")

        public Persona agregarExperienciaPortfolio (@PathVariable Long dni,@RequestBody String pExperiencia){

    //busco a la persona usando nuestra dependencia
  Persona perso= interPersona.findPersona(dni);
  
  //con el id de donde queremos agregar datos 
    //le asigno los valores que obtuvimos en el constructor de agregarInfoPortfolio
    //a los setters de la class Persona

perso.setExperiencia(pExperiencia);//tipo de datos de salida tipo String 
    
    
    //usando nuestra dependencia guardo los datos que acabamos de actualizar  en nuestra base de datos
    interPersona.agregarSavePersona(perso);
    //retorna la nueva persona
    return perso;
    
           
        }
   
           //endpoints 
        //borrar info portfolio
        @DeleteMapping("personas/borrarInfo/{dni}")
        public String deleteinfoPersona (@PathVariable Long dni){
               Persona perso= interPersona.findPersona(dni);
               perso.setInformacion("");
            interPersona.savePersona(perso);
                  //retorna un String avisando si elimino correctamente
            return "El campo  fue eliminada correctamente";
            
        }
                 //endpoints 
        //editar Experiencia Portfolio
        @PutMapping("personas/editarPortfolioExperiencia/{dni}")
        public Persona editInfoPortfolioExperiencia(@PathVariable Long dni,@RequestBody String pexperiencia){

    //busco a la persona usando nuestra dependencia
  Persona perso= interPersona.findPersona(dni);
  

  //con el id o campo de donde queremos haecr el update
    //le asigno los valores que obtuvimos en el constructor de editPersona
    //a los setters de la class Persona

perso.setExperiencia(pexperiencia);//tipo de datos de salida tipo String 
    
    
    //usando nuestra dependencia guardo los datos que acabamos de actualizar  en nuestra base de datos
    interPersona.savePersona(perso);
    //retorna la nueva persona
    return perso;
    
           
        }
   
        //borrar Experiencia
             @DeleteMapping("personas/borrarExperiencia/{dni}")
        public String deleteExperienciaPersona (@PathVariable Long dni){
               Persona perso= interPersona.findPersona(dni);
               perso.setExperiencia("");
            interPersona.savePersona(perso);
                  //retorna un String avisando si elimino correctamente
            return "El campo  fue eliminada correctamente";
            
        }

        //Editar Portfolio educacion
              @PutMapping("personas/editarPortfolioEducacion/{dni}")
        public Persona editarPortfolioEducacion(@PathVariable Long dni,@RequestBody String peducacion){

    //busco a la persona usando nuestra dependencia
  Persona perso= interPersona.findPersona(dni);
  

  

perso.setEducacion(peducacion);//tipo de datos de salida tipo String 
    
    
    //usando nuestra dependencia guardo los datos que acabamos de actualizar  en nuestra base de datos
    interPersona.savePersona(perso);
    //retorna la nueva persona
    return perso;
    
           
        }
   

        
        //borrar Educacion
        @DeleteMapping("personas/borrarEducacion/{dni}")
        public String deleteEducacionPersona (@PathVariable Long dni){
               Persona perso= interPersona.findPersona(dni);
               perso.setEducacion("");
            interPersona.savePersona(perso);
                  //retorna un String avisando si elimino correctamente
            return "El campo  fue eliminada correctamente";
            
        }
        
        
       
              // Función de inicio de sesión
        //usando @RequestBody 
 @PostMapping("/login")
    public boolean  Login(@RequestBody Persona perso){

         boolean login= interPersona.buscarPorNombre(perso.getNombre(),perso.getPassword());

    if(login==true){
         System.out.println("Inicio de Session");
    }
 System.out.println(login);
    


   return login;

    } 
        
        
   
   

         

}
