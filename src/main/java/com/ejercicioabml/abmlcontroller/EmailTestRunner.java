package com.ejercicioabml.abmlcontroller;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.abml.jpa.hibernate.service.EmailService;

@Component
public class EmailTestRunner implements CommandLineRunner {

    private final EmailService emailService;

    public EmailTestRunner(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.setProperty("javax.net.debug", "ssl,handshake");
        // Cambiá el destinatario por tu correo real
        String destinatario = "manuelbaidoxx6@gmail.com";
        String estado = "approved";
        String producto = "Notebook Lenovo";

        emailService.enviarNotificacion(destinatario, estado, producto);
    }
}
