package com.ChicaemeSAS.BackendSistema.service;

import com.ChicaemeSAS.BackendSistema.model.Usuario;
import com.ChicaemeSAS.BackendSistema.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuariosService {
    @Autowired
    private UsuarioRepository repository;

    public List<Usuario> findAll() {
        return repository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return repository.save(usuario);
    }

    public Usuario buscarUsuarioPorId(Long id) {
        // Usamos orElse(null) en lugar de .get() para evitar errores si el ID no existe
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
            usuarioExistente.setPassword(usuarioNuevo.getPassword());
            usuarioExistente.setTipoUsuario(usuarioNuevo.getTipoUsuario());
            usuarioExistente.setImagen(usuarioNuevo.getImagen());
            return repository.save(usuarioExistente);
        }).orElse(null);
    }

    // --- NUEVO MÉTODO PARA EL LOGIN ---
    public Usuario autenticarUsuario(String email, String password) {
        // Buscamos al usuario por correo
        Usuario usuario = buscarUsuarioPorEmail(email);

        // Si el usuario existe y la contraseña coincide, retornamos el usuario
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        }

        // Si no existe o la contraseña no coincide, retornamos null
        return null;
    }
}