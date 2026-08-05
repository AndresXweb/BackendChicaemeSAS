package com.ChicaemeSAS.BackendSistema.controller;

import com.ChicaemeSAS.BackendSistema.dto.LoginRequest;
import com.ChicaemeSAS.BackendSistema.model.Usuario;
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

    // 1. OBTENER TODOS
    @GetMapping
    public List<Usuario> getUsuarios() {
        return usuariosService.findAll();
    }

    // 2. CREAR
    @PostMapping
    public Usuario crearUsuario(@Valid @RequestBody Usuario creandoUsuario) {
        return usuariosService.guardarUsuario(creandoUsuario);
    }

    // 3. OBTENER POR ID
    @GetMapping("/{id}")
    public Usuario getUsuarioById(@PathVariable Long id) {
        return usuariosService.buscarUsuarioPorId(id);
    }

    // 4. ACTUALIZAR
    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable Long id, @RequestBody Usuario actualizandoUsuario) {
        return usuariosService.actualizarUsuario(id, actualizandoUsuario);
    }

    // 5. ELIMINAR
    @DeleteMapping("/{id}")
    public void eliminarUsuarioPorId(@PathVariable Long id) {
        usuariosService.eliminarUsuarioPorId(id);
    }

    // 6. LOGIN (Nuevo endpoint)
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequest loginRequest) {
        Usuario usuarioAutenticado = usuariosService.autenticarUsuario(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (usuarioAutenticado != null) {
            return ResponseEntity.ok(usuarioAutenticado);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo o contraseña incorrectos");
        }
    }
}