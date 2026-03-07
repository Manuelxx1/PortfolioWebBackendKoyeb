package com.ejercicioabml.abmlcontroller;
import com.abml.jpa.hibernate.service.TwoFactorRedisService;
import com.abml.jpa.hibernate.service.TwoFactorMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {

    @Autowired
    private TwoFactorRedisService twoFactorRedisService;

    @Autowired
    private TwoFactorMailService twoFactorMailService;

    // Endpoint para enviar el código
    @PostMapping("/send")
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        String code = twoFactorMailService.generateCode();
        twoFactorRedisService.saveCode(email, code);
        twoFactorMailService.sendCodeByEmail(email, code);
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
