
package com.abml.jpa.hibernate.service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class TwoFactorRedisService {

    private final StringRedisTemplate redisTemplate;

    public TwoFactorRedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set("2fa:" + email, code, 5, TimeUnit.MINUTES);
    }

    public boolean validateCode(String email, String code) {
        String stored = redisTemplate.opsForValue().get("2fa:" + email);
        return stored != null && stored.equals(code);
    }
}
