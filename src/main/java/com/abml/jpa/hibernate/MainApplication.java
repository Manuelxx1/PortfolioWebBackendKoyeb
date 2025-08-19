import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@SpringBootApplication
//para crear las tables en nuestra base de datos
//es fundamental las siguientes anotations
@EnableJpaRepositories (basePackages = "com.abml.jpa.hibernate.repository") //Escanea el repositorio bajo el paquete especificado

@EntityScan (basePackages = "com.abml.jpa.hibernate.model")//Escanea las entidades bajo el paquete especificado

@ComponentScan (basePackages = "com.abml.jpa.hibernate.service", "com.abml.jpa.hibernate.model", "com.ejercicioabml.abmlcontroller.NoticiaController",
"com.abml.jpa.hibernate.repository" ) // Especifica el paquete que se va a escanear, de lo contrario, solo el paquete donde se encuentra esta clase


public class AbmlcontrollerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbmlcontrollerApplication.class, args);
	}
        
