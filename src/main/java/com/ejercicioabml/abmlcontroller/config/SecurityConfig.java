package com.ejercicioabml.abmlcontroller.config;

import com.ejercicioabml.abmlcontroller.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//AuthenticationConfiguration correcto
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;


//import org.springframework.security.authentication.AuthenticationConfiguration;


//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          // 1. Deshabilita CSRF porque es una API stateless
          .csrf(csrf -> csrf.disable())
          
          // 2. Define qué rutas permites sin autenticación
          .authorizeHttpRequests(auth -> auth
              // endpoints públicos
              .requestMatchers("/login","/loginsinjwt","/register","/loginsinjwteshop","/registereshop").permitAll()
              
              // endpoint protegido: requiere JWT válido
              .requestMatchers("/profile").authenticated()
              
              // TODO: si tienes más rutas que quieres públicas, añádelas aquí
              
              // el resto: público
              .anyRequest().permitAll()
          )
          
          // 3. Stateless: sin sesión HTTP
          .sessionManagement(sm -> sm
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          );
        
        // 4. Inserta el filtro de JWT antes del filtro de usuario/clave
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
/*
desactivado por recursion
Al eliminar ese método, Spring inyectará internamente el auténtico ProviderManager (no un proxy de sí mismo),
con lo que authManager.authenticate(...) dejará de entrar en recursión.

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    */

/**
     * Este bean le indica a Spring cómo obtener el AuthenticationManager real
     * de la configuración de seguridad ya creada.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) 
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    
}
