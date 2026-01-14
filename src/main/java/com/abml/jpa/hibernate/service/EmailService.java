import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarNotificacion(String destinatario, String estado, String producto) {
        String asunto;
        String mensaje;

        switch (estado) {
            case "approved":
                asunto = "Pago aprobado ✅";
                mensaje = "Tu compra de " + producto + " fue aprobada. ¡Gracias por confiar en nosotros!";
                break;
            case "rejected":
                asunto = "Pago rechazado ❌";
                mensaje = "Tu compra de " + producto + " fue rechazada. Podés intentar nuevamente.";
                break;
            default:
                asunto = "Pago pendiente ⏳";
                mensaje = "Tu compra de " + producto + " está pendiente. Te avisaremos cuando se confirme.";
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("manuelbaidoxx6@gmail.com");
        mailMessage.setTo(destinatario);
        mailMessage.setSubject(asunto);
        mailMessage.setText(mensaje);

        mailSender.send(mailMessage);
    }
}
