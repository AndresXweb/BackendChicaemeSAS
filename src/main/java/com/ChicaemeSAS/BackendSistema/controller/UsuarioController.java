package com.ChicaemeSAS.BackendSistema.controller;

import com.ChicaemeSAS.BackendSistema.dto.GoogleLoginRequest;
import com.ChicaemeSAS.BackendSistema.dto.LoginRequest;
import com.ChicaemeSAS.BackendSistema.dto.LoginResponse;
import com.ChicaemeSAS.BackendSistema.dto.UsuarioResponseDTO;
import com.ChicaemeSAS.BackendSistema.model.Usuario;
import com.ChicaemeSAS.BackendSistema.security.JwtUtil;
import com.ChicaemeSAS.BackendSistema.service.EmailService;
import com.ChicaemeSAS.BackendSistema.service.GoogleAuthService;
import com.ChicaemeSAS.BackendSistema.service.UsuariosService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuariosService usuariosService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private GoogleAuthService googleAuthService;

    // 1. OBTENER TODOS (sin password)
    @GetMapping
    public List<UsuarioResponseDTO> getUsuarios() {
        return usuariosService.findAll().stream()
                .map(UsuarioResponseDTO::fromUsuario)
                .toList();
    }

    // 2. CREAR (registro público, o creación desde el panel admin)
    @PostMapping
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody Usuario creandoUsuario) {

        // ¿Quien llama es un admin ya logueado (panel de Gestión de Usuarios)?
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Usuario guardado;
        try {
            if (esAdmin) {
                // Admin autenticado: se respeta el tipoUsuario que mandó el formulario del panel.
                guardado = usuariosService.guardarUsuario(creandoUsuario);
            } else {
                // Registro público: misma regla (forzar Cliente + exigir términos) que
                // comparte el flujo de Google Sign-In. Vive en UsuariosService para no duplicarla.
                guardado = usuariosService.registrarUsuarioPublico(
                        creandoUsuario,
                        Boolean.TRUE.equals(creandoUsuario.getAceptoTerminos())
                );
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponseDTO.fromUsuario(guardado));
    }

    // 3. OBTENER POR ID (sin password)
    @GetMapping("/{id}")
    public UsuarioResponseDTO getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuariosService.buscarUsuarioPorId(id);
        return usuario != null ? UsuarioResponseDTO.fromUsuario(usuario) : null;
    }

    // 4. ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario actualizandoUsuario) {
        try {
            Usuario actualizado = usuariosService.actualizarUsuario(id, actualizandoUsuario);
            return actualizado != null
                    ? ResponseEntity.ok(UsuarioResponseDTO.fromUsuario(actualizado))
                    : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 5. ELIMINAR
    @DeleteMapping("/{id}")
    public void eliminarUsuarioPorId(@PathVariable Long id) {
        usuariosService.eliminarUsuarioPorId(id);
    }

    // 6. LOGIN: ahora devuelve token + datos SIN password
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequest loginRequest) {
        Usuario usuarioAutenticado = usuariosService.autenticarUsuario(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (usuarioAutenticado != null) {
            String token = jwtUtil.generarToken(usuarioAutenticado.getEmail(), usuarioAutenticado.getTipoUsuario());
            LoginResponse respuesta = new LoginResponse(token, UsuarioResponseDTO.fromUsuario(usuarioAutenticado));
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos");
        }
    }

    // 7. OLVIDÉ MI CONTRASEÑA: genera el token y envía el correo
    @PostMapping("/forgot-password")
    public ResponseEntity<?> olvidoPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String token = usuariosService.generarTokenRecuperacion(email);

        if (token != null) {
            emailService.enviarCorreoRecuperacion(email, token);
        }

        // Respuesta siempre igual, exista o no el correo: evita que este endpoint
        // se use para averiguar qué correos están registrados en el sistema.
        return ResponseEntity.ok("Si el correo existe en nuestro sistema, te llegará un enlace de recuperación.");
    }

    // 8. RESTABLECER CONTRASEÑA: valida el token del correo y guarda la nueva
    @PostMapping("/reset-password")
    public ResponseEntity<?> restablecerPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String nuevaPassword = body.get("password");

        boolean exito;
        try {
            exito = usuariosService.restablecerPassword(token, nuevaPassword);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        if (exito) {
            return ResponseEntity.ok("Contraseña actualizada correctamente.");
        } else {
            return ResponseEntity.badRequest().body("El enlace no es válido o ya expiró. Solicita uno nuevo.");
        }
    }

    // 9. LOGIN / REGISTRO CON GOOGLE
    // El frontend manda el idToken que le entregó Google (accounts.google.com/gsi/client).
    // Nunca confiamos en email/nombre si vinieran sueltos en el body: todo sale de
    // verificar el token contra Google dentro de GoogleAuthService.
    @PostMapping("/google-login")
    public ResponseEntity<?> loginConGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        GoogleIdToken.Payload payload = googleAuthService.verificarToken(request.getIdToken());

        if (payload == null || !Boolean.TRUE.equals(payload.getEmailVerified())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token de Google inválido, expirado, o correo no verificado.");
        }

        String email = payload.getEmail();
        String nombres = (String) payload.get("given_name");
        String apellidos = (String) payload.get("family_name");

        Usuario usuario;
        try {
            usuario = usuariosService.autenticarOCrearConGoogle(
                    email, nombres, apellidos, Boolean.TRUE.equals(request.getAceptoTerminos())
            );
        } catch (IllegalArgumentException e) {
            // Solo ocurre cuando el correo NO existía todavía y no llegaron los términos aceptados.
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getTipoUsuario());
        LoginResponse respuesta = new LoginResponse(token, UsuarioResponseDTO.fromUsuario(usuario));
        return ResponseEntity.ok(respuesta);
    }
}
