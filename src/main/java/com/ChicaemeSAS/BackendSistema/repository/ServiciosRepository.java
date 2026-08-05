package com.ChicaemeSAS.BackendSistema.repository;

import com.ChicaemeSAS.BackendSistema.model.servicios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiciosRepository extends JpaRepository<servicios, Long> {
    // ^ REVISA QUE DIGA 'public' AQUÍ
}