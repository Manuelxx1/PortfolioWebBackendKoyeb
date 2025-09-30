
package com.abml.jpa.hibernate.security;
import com.abml.jpa.hibernate.model.Persona;
import com.abml.jpa.hibernate.service.PersonaService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


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
