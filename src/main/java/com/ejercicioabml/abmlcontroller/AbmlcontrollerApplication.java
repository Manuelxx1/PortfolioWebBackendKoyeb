
package com.ejercicioabml.abmlcontroller;

import com.abml.jpa.hibernate.model.Persona;
import com.abml.jpa.hibernate.repository.PersonaRepository;
import com.abml.jpa.hibernate.service.PersonaService;
import com.ejercicioabml.abmlcontroller.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;



//luego de haber creado la table ya es posibles realizar el ABML en la base de datos usando los endpoints
@RestController 
//@CrossOrigin afecta el funcionamiento de Postman
//@CrossOrigin es fundamental para conectar angular con el backend Springboot
@CrossOrigin(origins = "https://4200-cs-582739288523-default.cs-us-east1-yeah.cloudshell.dev")





public class AbmlcontrollerApplication{
                                        
    private final AuthenticationManager authManager;
   // private final PersonaService interPersona;
    private final JwtUtil jwtUtil;

    public AbmlcontrollerApplication(AuthenticationManager authManager,JwtUtil jwtUtil) {
    
                                     
        this.authManager = authManager;
        
        this.jwtUtil = jwtUtil;
    }

        
        //con Autowired inyectamos la dependecia que queremos usar sin crear un objeto instancia de esa class
        
        
//en la variable interPersona se guarda los datos de la class PersonaService
                //que es la encargada de llamar a JPARepositories
  @Autowired  private PersonaService interPersona ;
                
                
       //@Autowired private JwtUtil jwtUtil;
//@Autowired private AuthenticationManager authManager;
        
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
        
        
       /*
              // Función de inicio de sesión
        //usando @RequestBody 
 @PostMapping("/loginsinjwt")
    public boolean  Login(@RequestBody Persona perso){

         boolean login= interPersona.buscarPorNombre(perso.getNombre(),perso.getPassword());

    if(login==true){
         System.out.println("Inicio de Session");
    }
 System.out.println(login);
    


   return login;

    } 
        */


  //login sinjwt copilot
//a pesar que no se usa jwt se usa unas clasess
  //de springsecurity como BCryptPasswordEncoder en securityconfig  para codificar
  //la contraseña que llega del form para insertar 
  //eso en base de datos y no quede expuesto el dato
@PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Persona user) {
        return ResponseEntity.ok(interPersona.save(user));
    }
//llamado al repository directo sin usar service
    

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/loginsinjwt")
    public ResponseEntity<?> login(@RequestBody Persona user) {
        Optional<Persona> personaOpt = personaRepository.findByNombre(user.getNombre());
//se verifica si el usuario existe y se guarda
      //en personaEnBD
        if (personaOpt.isPresent()) {
            Persona personaEnBD = personaOpt.get();
//aquí la lógica de verificación si la contraseña
          //que llega desde el form es igual 
          // al de la db 
          //como la contraseña de la db está codificada 
          //se utiliza passwordEncoder para codificar 
          //lo que llegó del form y luego comparar con matches
          //si los datos codificados son iguales 
          //resultado true "mensaje", "Login exitoso" 
          //o false "error", "Nombre o contraseña incorrectos"
            if (passwordEncoder.matches(user.getPassword(), personaEnBD.getPassword())) {
                return ResponseEntity.ok(Map.of(
                    "mensaje", "Login exitoso",
                    "usuario", personaEnBD.getNombre()
                ));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            Map.of("error", "Nombre o contraseña incorrectos")
        );
    }




@Autowired
private PersonaRepository personaRepository;

@Value("${base.url}")
    private String baseUrl;


private static final Map<String, List<String>> archivoPorPalabra = Map.of(
    "agua", List.of("agua.html", "explicacion.html"),
    "informacion", List.of("agua.html", "explicacion.html","planetatierra.html"),
    "sistema", List.of("sistema.html", "explicacion.html"),
    "solar", List.of("solar.html", "explicacion.html"),
"planeta", List.of( "planetatierra.html"),
"tierra", List.of( "planetatierra.html")
        
);

        //map ampliado a varios HTML
       @GetMapping("/html-link")
public ResponseEntity<String> obtenerLinkHtml(@RequestParam String frase) {
    List<Persona> personas = personaRepository.findAll();
    String fraseNormalizada = quitarAcentos(frase.toLowerCase());
    String[] palabras = fraseNormalizada.split(" ");

    Set<String> archivosCoincidentes = new LinkedHashSet<>();

    for (Persona persona : personas) {
        String infoNormalizada = quitarAcentos(persona.getInformacion().toLowerCase());

        for (String palabra : palabras) {
            if (infoNormalizada.contains(palabra)) {
                List<String> archivos = archivoPorPalabra.get(palabra);
                if (archivos != null) {
                    archivosCoincidentes.addAll(archivos);
                }
            }
        }
    }

    if (archivosCoincidentes.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("No se encontró información que contenga esa palabra.");
    }

    // Generar HTML con todos los enlaces encontrados
    StringBuilder htmlBuilder = new StringBuilder("<html><body>");
    for (String archivo : archivosCoincidentes) {
        String urlCompleta = baseUrl + archivo;
        htmlBuilder.append("<a href=\"").append(urlCompleta)
                   .append("\" target=\"_blank\">Ver ").append(archivo.replace(".html", ""))
                   .append("</a><br>");
    }
    htmlBuilder.append("</body></html>");

    return ResponseEntity.ok(htmlBuilder.toString());
}
 


 /*       
 Para el filtrado por Map pero limitado
@GetMapping("/html-link")
public ResponseEntity<String> obtenerLinkHtml(@RequestParam String frase) {
    List<Persona> personas = personaRepository.findAll();

    String fraseNormalizada = quitarAcentos(frase.toLowerCase());
    String[] palabras = fraseNormalizada.split(" ");

    for (Persona persona : personas) {
        String infoNormalizada = quitarAcentos(persona.getInformacion().toLowerCase());

        for (String palabra : palabras) {
            if (infoNormalizada.contains(palabra)) {
                String archivoHtml = archivoPorPalabra.getOrDefault(palabra, "explicacion.html");
                String urlCompleta = baseUrl + archivoHtml;
                String htmlLink = "<html><body><a href=\"" + urlCompleta + "\" target=\"_blank\">Ver explicación</a></body></html>";
                return ResponseEntity.ok(htmlLink);
            }
        }
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("No se encontró información que contenga esa palabra.");
}

*/

        
/*
Para el simple retorna explicación.html
@GetMapping("/html-link")
public ResponseEntity<String> obtenerLinkHtml(@RequestParam String frase) {
    List<Persona> personas = personaRepository.findAll();

    // Normalizamos la frase del usuario (sin acentos, en minúscula)
    String fraseNormalizada = quitarAcentos(frase.toLowerCase());
    String[] palabras = fraseNormalizada.split(" ");

    for (Persona persona : personas) {
        // Normalizamos también la información de la persona
        String infoNormalizada = quitarAcentos(persona.getInformacion().toLowerCase());

        for (String palabra : palabras) {
            if (infoNormalizada.contains(palabra)) {
                String urlCompleta = baseUrl + "explicacion.html";
                String htmlLink = "<html><body><a href=\"" + urlCompleta + "\" target=\"_blank\">Ver explicación</a></body></html>";
                return ResponseEntity.ok(htmlLink);
            }
        }
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("No se encontró información que contenga esa palabra.");
}
        */

// Función para quitar acentos
private String quitarAcentos(String texto) {
    return Normalizer.normalize(texto, Normalizer.Form.NFD)
                     .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
}

        // Archivo de tu controlador de Spring Boot
// ...
@GetMapping("/test-param")
public ResponseEntity<String> testearParametro(@RequestParam String frase) {
    System.out.println("Parámetro recibido: '" + frase + "'");
    return ResponseEntity.ok("Recibido: " + frase);
}

        //login jwt copilot
/*
        
@PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Persona user) {
        return ResponseEntity.ok(interPersona.save(user));
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Persona user) {
    authManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            user.getNombre(),
            user.getPassword()
        )
    );
    String token = jwtUtil.generateToken(user.getNombre());
    return ResponseEntity.ok(Map.of("token", token));
}


    @GetMapping("/profile")
    public ResponseEntity<?> profile() {
        return ResponseEntity.ok("Acceso autorizado al perfil");
    }
  */

}
