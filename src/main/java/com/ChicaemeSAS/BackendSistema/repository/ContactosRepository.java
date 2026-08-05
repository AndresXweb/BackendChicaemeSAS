package com.ChicaemeSAS.BackendSistema.repository;

import com.ChicaemeSAS.BackendSistema.model.Contactos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactosRepository extends JpaRepository<Contactos, Long> {

    // Encontrar todos los contactos de un usuario específico
    List<Contactos> findByUsuarioId(Long usuarioId);

    // Encontrar contactos por tipo
    List<Contactos> findByTipoContacto(String tipoContacto);

    // Encontrar contactos por email
    List<Contactos> findByEmail(String email);
}