

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
        try {
            String mensaje = "🔔 Sync: " + System.currentTimeMillis();
            System.out.println("Intentando enviar a /topic/notificaciones...");
            
            // Forzamos el envío
            template.convertAndSend("/topic/notificaciones", mensaje);
            
            System.out.println("¡Envío completado sin errores!");
        } catch (Exception e) {
            System.err.println("Error al enviar websocket: " + e.getMessage());
        }
    }

}

