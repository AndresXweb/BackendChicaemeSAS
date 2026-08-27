package com.ChicaemeSAS.BackendSistema.controller;

import com.ChicaemeSAS.BackendSistema.dto.LoginRequest;
import com.ChicaemeSAS.BackendSistema.dto.LoginResponse;
import com.ChicaemeSAS.BackendSistema.dto.UsuarioResponseDTO;
import com.ChicaemeSAS.BackendSistema.model.Usuario;
import com.ChicaemeSAS.BackendSistema.security.JwtUtil;
import com.ChicaemeSAS.BackendSistema.service.EmailService;
import com.ChicaemeSAS.BackendSistema.service.UsuariosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

        if (!esAdmin) {
            // Registro público: exige haber aceptado términos, e ignora cualquier
            // tipoUsuario que venga en el body (evita que alguien se auto-asigne admin).
            if (!Boolean.TRUE.equals(creandoUsuario.getAceptoTerminos())) {
                return ResponseEntity.badRequest().body("Debes aceptar los términos y condiciones para registrarte.");
            }
            creandoUsuario.setTipoUsuario("Cliente");
            creandoUsuario.setFechaAceptacionTerminos(LocalDateTime.now());
        }
        // Si es admin autenticado, se respeta el tipoUsuario que mandó el formulario del panel.

        Usuario guardado = usuariosService.guardarUsuario(creandoUsuario);
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
    public UsuarioResponseDTO actualizarUsuario(@PathVariable Long id, @RequestBody Usuario actualizandoUsuario) {
        Usuario actualizado = usuariosService.actualizarUsuario(id, actualizandoUsuario);
        return actualizado != null ? UsuarioResponseDTO.fromUsuario(actualizado) : null;
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

        boolean exito = usuariosService.restablecerPassword(token, nuevaPassword);

        if (exito) {
            return ResponseEntity.ok("Contraseña actualizada correctamente.");
        } else {
            return ResponseEntity.badRequest().body("El enlace no es válido o ya expiró. Solicita uno nuevo.");
        }
    }
}
