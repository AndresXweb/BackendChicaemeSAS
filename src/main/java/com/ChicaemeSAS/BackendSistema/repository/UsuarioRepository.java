package com.ChicaemeSAS.BackendSistema.repository;

import com.ChicaemeSAS.BackendSistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Spring entiende que debe hacer: SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByTelefono(String telefono);
    Optional<Usuario> findByResetToken(String resetToken);
}