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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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

    // CORS registrado DENTRO de Spring Security (no solo en CorsConfig.java aparte).
    // Así, hasta las respuestas de rechazo (401/403) llevan el header Access-Control-Allow-Origin,
    // y el navegador puede mostrar el error real en vez de reportarlo como "bloqueado por CORS".
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // URL real del frontend en desarrollo
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enganchamos el bean de arriba directamente en Spring Security
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Desactivamos CSRF: es protección de navegador con cookies/sesión,
                // acá usamos JWT sin estado (stateless), no aplica.
                .csrf(csrf -> csrf.disable())

                // Sin sesiones en el servidor: cada petición se valida con su propio token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // ---------- PREFLIGHT DE CORS: siempre libre, o el navegador bloquea todo ----------
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ---------- RUTAS PÚBLICAS (sin login) ----------
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/google-login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/reset-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()      // registro
                        .requestMatchers(HttpMethod.POST, "/api/contactos").permitAll()     // formulario de contacto
                        .requestMatchers(HttpMethod.GET, "/api/articulos/**").permitAll()   // catálogo público
                        .requestMatchers(HttpMethod.GET, "/api/servicios/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()          // ver las fotos ya subidas
                        .requestMatchers(HttpMethod.POST, "/api/archivos/imagen").permitAll() // subir una foto (el registro pasa antes de tener token)

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
