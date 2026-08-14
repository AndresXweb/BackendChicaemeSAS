package com.ChicaemeSAS.BackendSistema.security;

import com.ChicaemeSAS.BackendSistema.model.Usuario;
import com.ChicaemeSAS.BackendSistema.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe un usuario con ese correo: " + email));

        return new User(
                usuario.getEmail(),
                usuario.getPassword(), // ya viene hasheada desde la BD, no en texto plano
                List.of(new SimpleGrantedAuthority(mapearRol(usuario.getTipoUsuario())))
        );
    }

    // El frontend guarda tipoUsuario como "Administrador", "Cliente" o "Staff" (así está
    // el <select> en Usuarios.jsx). SecurityConfig espera roles en inglés (ADMIN, etc.),
    // así que traducimos acá en un solo lugar en vez de acoplar SecurityConfig al texto en español.
    private String mapearRol(String tipoUsuario) {
        if (tipoUsuario == null) {
            return "ROLE_CLIENTE";
        }
        return switch (tipoUsuario.trim().toLowerCase()) {
            case "administrador", "admin" -> "ROLE_ADMIN";
            case "staff", "empleado" -> "ROLE_STAFF";
            default -> "ROLE_CLIENTE";
        };
    }
}