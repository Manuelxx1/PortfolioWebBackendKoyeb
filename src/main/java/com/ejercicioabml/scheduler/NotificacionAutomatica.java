
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificacionAutomatica {

    @Autowired
    private SimpMessagingTemplate template;

    // Enviar cada 10 segundos
    @Scheduled(fixedRate = 10000)
    public void enviarAutomatico() {
        String mensaje = "🔔 Notificación automática desde el servidor: " + System.currentTimeMillis();
        template.convertAndSend("/topic/notificaciones", mensaje);
    }
}
