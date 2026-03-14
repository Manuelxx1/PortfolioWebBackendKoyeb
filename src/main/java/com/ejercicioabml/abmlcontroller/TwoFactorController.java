package com.ejercicioabml.abmlcontroller;
import com.abml.jpa.hibernate.service.TwoFactorRedisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.SecureRandom;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {

    @Autowired
    private TwoFactorRedisService twoFactorRedisService;


    private final SecureRandom random = new SecureRandom();

private String generateCode() {
    int code = 100000 + random.nextInt(900000); // 6 dígitos
    return String.valueOf(code);
}

    
    // Endpoint para enviar el código
    @PostMapping("/send")
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        String code = generateCode();
    twoFactorRedisService.saveCode(email, code);
      
        
        // Llamada al microservicio en Termux vía túnel
    String url = "https://wealth-indicating-sponsors-bra.trycloudflare.com/api/send?email=" + email + "&code=" + code;
        RestTemplate restTemplate = new RestTemplate();
    restTemplate.postForObject(url, null, String.class);
        return ResponseEntity.ok("Código enviado a " + email);
    }

    // Endpoint para validar el código
    @PostMapping("/validate")
public ResponseEntity<Map<String, Object>> validateCode(@RequestParam String email, @RequestParam String code) {
    System.out.println("Entró a validateCode");
    boolean valid = twoFactorRedisService.validateCode(email, code);
    if (valid) {
        Users user = userRepository.findByEmail(email).orElseThrow();

        return ResponseEntity.ok(Map.of(
            "mensaje", "Código válido, acceso permitido",
            "id", user.getId(),
            "usuario", user.getUsername(),
            "email", user.getEmail(),
            "name", user.getName(),
            "createdAt", user.getcreatedAt()
        ));
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(Map.of("error", "Código inválido o expirado"));
    }
}


}
