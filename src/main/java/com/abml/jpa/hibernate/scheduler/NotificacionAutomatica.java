

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificacionAutomatica {

    @Autowired
    private SimpMessagingTemplate template;

    // Enviar cada 10 segundos

@Scheduled(fixedRate = 10000) // 10 segundos
    public void enviarAutomatico() {
        String mensaje = "HOLA DESDE SPRING " + System.currentTimeMillis();
        System.out.println(">>> EJECUTANDO SCHEDULER: " + mensaje);
        this.template.convertAndSend("/topic/notificaciones", mensaje);
    }

}

