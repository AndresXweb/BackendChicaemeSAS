package com.ChicaemeSAS.BackendSistema.controller;

import com.ChicaemeSAS.BackendSistema.dto.LoginRequest;
import com.ChicaemeSAS.BackendSistema.dto.LoginResponse;
import com.ChicaemeSAS.BackendSistema.dto.UsuarioResponseDTO;
import com.ChicaemeSAS.BackendSistema.model.Usuario;
import com.ChicaemeSAS.BackendSistema.security.JwtUtil;
import com.ChicaemeSAS.BackendSistema.service.UsuariosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuariosService usuariosService;

    @Autowired
    private JwtUtil jwtUtil;

    // 1. OBTENER TODOS (sin password)
    @GetMapping
    public List<UsuarioResponseDTO> getUsuarios() {
        return usuariosService.findAll().stream()
                .map(UsuarioResponseDTO::fromUsuario)
                .toList();
    }

    // 2. CREAR (registro)
    @PostMapping
    public UsuarioResponseDTO crearUsuario(@Valid @RequestBody Usuario creandoUsuario) {
        Usuario guardado = usuariosService.guardarUsuario(creandoUsuario);
        return UsuarioResponseDTO.fromUsuario(guardado);
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
}
