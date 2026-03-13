package com.abml.jpa.hibernate.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestPassword {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "Unodostrescatorce";
        String hashedPassword = "$2a$10$TrzYiTOSXDf7QChkNt9H2u.rAatsBgWJHn3XCmiD.B6nfvwnhuDMS";

        boolean matches = encoder.matches(rawPassword, hashedPassword);

        System.out.println("¿Coincide? " + matches);
    }
}
