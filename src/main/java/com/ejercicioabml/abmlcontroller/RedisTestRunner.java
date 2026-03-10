package com.ejercicioabml.abmlcontroller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisTestRunner implements CommandLineRunner {

    private final StringRedisTemplate redisTemplate;

    public RedisTestRunner(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            // Guardar un valor de prueba
            redisTemplate.opsForValue().set("test:key", "Hola Redis!", 30, TimeUnit.SECONDS);

            // Leer el valor
            String value = redisTemplate.opsForValue().get("test:key");
            System.out.println("✅ Conexión a Redis OK, valor leído: " + value);
        } catch (Exception e) {
            System.err.println("❌ Error conectando a Redis: " + e.getMessage());
        }
    }
}
