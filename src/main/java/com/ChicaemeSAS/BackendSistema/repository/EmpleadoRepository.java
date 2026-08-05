package com.ChicaemeSAS.BackendSistema.repository;

import com.ChicaemeSAS.BackendSistema.model.Empleados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface EmpleadoRepository extends JpaRepository<Empleados, Long>{
}
