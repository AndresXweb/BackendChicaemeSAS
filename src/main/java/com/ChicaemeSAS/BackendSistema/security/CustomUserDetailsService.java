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

        // tipoUsuario ("admin", "cliente", etc.) se convierte en un rol de Spring Security: ROLE_ADMIN, ROLE_CLIENTE...
        String rol = "ROLE_" + usuario.getTipoUsuario().toUpperCase();

        return new User(
                usuario.getEmail(),
                usuario.getPassword(), // ya viene hasheada desde la BD, no en texto plano
                List.of(new SimpleGrantedAuthority(rol))
        );
    }
}
