package com.ChicaemeSAS.BackendSistema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactivamos CSRF. Es una protección para navegadores, pero bloquea Postman.
                .csrf(csrf -> csrf.disable())

                // Configuramos los permisos de las rutas
                .authorizeHttpRequests(auth -> auth
                        // Por ahora, le decimos que permita TODAS las peticiones sin contraseña
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}