package com.dam.accesodatos.sistemacrudloginra3_irisperez.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Configuración temporal (sin seguridad)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request ->
                request.anyRequest().permitAll() // Permitir entrar a todos lados sin login
        ).csrf(csrf -> csrf.disable()); // Deshabilitar protección CSRF para pruebas

        return http.build();

    }
}