package com.ChicaemeSAS.BackendSistema.service;

import com.ChicaemeSAS.BackendSistema.model.Usuario;
import com.ChicaemeSAS.BackendSistema.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuariosService {
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> findAll() {
        return repository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Con el campo como Boolean (wrapper) ya puede llegar null sin romper el
        // JSON, pero la columna en la BD sigue siendo NOT NULL — así que si llega
        // vacío (ej: el panel admin no manda este campo), lo dejamos en false.
        if (usuario.getAceptoTerminos() == null) {
            usuario.setAceptoTerminos(false);
        }

        return repository.save(usuario);
    }

    public Usuario buscarUsuarioPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public Usuario buscarUsuarioPorTelefono(String telefono) {
        return repository.findByTelefono(telefono).orElse(null);
    }

    public void eliminarUsuarioPorId(Long id) {
        repository.deleteById(id);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioNuevo) {
        return repository.findById(id).map(usuarioExistente -> {
            usuarioExistente.setNombres(usuarioNuevo.getNombres());
            usuarioExistente.setApellidos(usuarioNuevo.getApellidos());
            usuarioExistente.setDireccion(usuarioNuevo.getDireccion());
            usuarioExistente.setCiudad(usuarioNuevo.getCiudad());
            usuarioExistente.setTelefono(usuarioNuevo.getTelefono());
            usuarioExistente.setEmail(usuarioNuevo.getEmail());

            // Solo re-hasheamos si mandaron una contraseña nueva.
            // Si el campo viene vacío/null, dejamos la contraseña actual sin tocar.
            if (usuarioNuevo.getPassword() != null && !usuarioNuevo.getPassword().isBlank()) {
                usuarioExistente.setPassword(passwordEncoder.encode(usuarioNuevo.getPassword()));
            }

            usuarioExistente.setTipoUsuario(usuarioNuevo.getTipoUsuario());
            usuarioExistente.setImagen(usuarioNuevo.getImagen());
            return repository.save(usuarioExistente);
        }).orElse(null);
    }

    // --- LOGIN: ahora compara con BCrypt en vez de .equals() ---
    public Usuario autenticarUsuario(String email, String password) {
        Usuario usuario = buscarUsuarioPorEmail(email);

        if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
            return usuario;
        }

        return null;
    }

    // --- RECUPERACIÓN DE CONTRASEÑA ---

    // Devuelve el token generado, o null si el correo no existe.
    // El controller decide qué responder al frontend (siempre el mismo mensaje, exista o no el correo).
    public String generarTokenRecuperacion(String email) {
        Usuario usuario = buscarUsuarioPorEmail(email);
        if (usuario == null) {
            return null;
        }

        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setResetTokenExpira(LocalDateTime.now().plusMinutes(30));
        repository.save(usuario);
        return token;
    }

    // Devuelve true si el token era válido y la contraseña quedó actualizada.
    public boolean restablecerPassword(String token, String nuevaPassword) {
        Usuario usuario = repository.findByResetToken(token).orElse(null);

        if (usuario == null || usuario.getResetTokenExpira() == null
                || usuario.getResetTokenExpira().isBefore(LocalDateTime.now())) {
            return false; // token inexistente, ya usado, o expirado
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setResetToken(null);
        usuario.setResetTokenExpira(null);
        repository.save(usuario);
        return true;
    }
}
