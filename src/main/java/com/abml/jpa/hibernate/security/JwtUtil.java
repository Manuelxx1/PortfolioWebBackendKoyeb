
package com.abml.jpa.hibernate.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;

import org.springframework.stereotype.Component;

import java.util.Date;



@Component
public class JwtUtil {
  private final String SECRET = "clave_secreta";

  public String generateToken(String username) {
    return Jwts.builder()
      .setSubject(username)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + 3600000))
      .signWith(SignatureAlgorithm.HS256, SECRET)
      .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateToken(String token, String username) {
    return extractUsername(token).equals(username) && !isExpired(token);
  }

  private boolean isExpired(String token) {
    Date expiration = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getExpiration();
    return expiration.before(new Date());
  }
}
