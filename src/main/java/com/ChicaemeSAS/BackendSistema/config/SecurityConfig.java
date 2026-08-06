package com.ChicaemeSAS.BackendSistema.config;

import com.ChicaemeSAS.BackendSistema.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    // Encriptador de contraseñas. Se usa en UsuariosService para hashear y comparar.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Necesario si en algún momento quieren autenticar manualmente (no lo usamos todavía,
    // pero Spring Security lo pide disponible como bean en configuraciones con JWT propio).
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactivamos CSRF: es protección de navegador con cookies/sesión,
                // acá usamos JWT sin estado (stateless), no aplica.
                .csrf(csrf -> csrf.disable())

                // Sin sesiones en el servidor: cada petición se valida con su propio token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // ---------- RUTAS PÚBLICAS (sin login) ----------
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()      // registro
                        .requestMatchers(HttpMethod.POST, "/api/contactos").permitAll()     // formulario de contacto
                        .requestMatchers(HttpMethod.GET, "/api/articulos/**").permitAll()   // catálogo público
                        .requestMatchers(HttpMethod.GET, "/api/servicios/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()

                        // ---------- RUTAS SOLO PARA ADMIN ----------
                        .requestMatchers("/api/empleados/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")       // listar todos
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/contactos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/articulos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/articulos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/articulos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/servicios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/servicios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/servicios/**").hasRole("ADMIN")
                        .requestMatchers("/api/categorias/**").hasRole("ADMIN") // POST/PUT/DELETE (el GET ya quedó público arriba)

                        // ---------- CUALQUIER OTRA RUTA: requiere estar logueado ----------
                        // cotizaciones, pagos, ver/editar perfil propio (/api/usuarios/{id}), etc.
                        .anyRequest().authenticated()
                )

                // Nuestro filtro revisa el token ANTES del filtro estándar de Spring Security
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
