package com.ChicaemeSAS.BackendSistema.repository;

import com.ChicaemeSAS.BackendSistema.model.ArticuloAlquiler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticuloAlquilerRepository extends JpaRepository<ArticuloAlquiler, Long> {
    // Al heredar de JpaRepository, Spring Boot nos regala automáticamente
    // los métodos save(), findAll(), findById(), deleteById() sin escribir SQL.
}

