package com.ChicaemeSAS.BackendSistema.service;

import com.ChicaemeSAS.BackendSistema.model.Usuario;
import com.ChicaemeSAS.BackendSistema.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        // Hasheamos la contraseña ANTES de guardar. Nunca se guarda en texto plano.
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
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
}
