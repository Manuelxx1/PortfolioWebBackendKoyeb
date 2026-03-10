package com.ejercicioabml.abmlcontroller;
import com.abml.jpa.hibernate.service.TwoFactorRedisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {

    @Autowired
    private TwoFactorRedisService twoFactorRedisService;

    // Endpoint para enviar el código
    @PostMapping("/send")
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        String code = twoFactorRedisService.generateCode(); // genera el código
    twoFactorRedisService.saveCode(email, code);        // guarda en Redis
        
        // Llamada al microservicio en Termux vía túnel
    String url = "https://b2488e5afca48e.lhr.life/api/send?email=" + email + "&code=" + code;
    restTemplate.postForObject(url, null, String.class);
        return ResponseEntity.ok("Código enviado a " + email);
    }

    // Endpoint para validar el código
    @PostMapping("/validate")
    public ResponseEntity<String> validateCode(@RequestParam String email, @RequestParam String code) {
        boolean valid = twoFactorRedisService.validateCode(email, code);
        if (valid) {
            return ResponseEntity.ok("Código válido, acceso permitido");
        } else {
            return ResponseEntity.status(401).body("Código inválido o expirado");
        }
    }
}
