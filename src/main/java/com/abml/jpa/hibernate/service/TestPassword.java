package com.abml.jpa.hibernate.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPassword {
    private static final Logger log = LoggerFactory.getLogger(TestPassword.class);
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "Unodostrescatorce";
        String hashedPassword = "$2a$10$TrzYiTOSXDf7QChkNt9H2u.rAatsBgWJHn3XCmiD.B6nfvwnhuDMS";

        boolean matches = encoder.matches(rawPassword, hashedPassword);
log.info("¿Coincide? " + matches);
        System.out.println("¿Coincide? " + matches);
    }
}
