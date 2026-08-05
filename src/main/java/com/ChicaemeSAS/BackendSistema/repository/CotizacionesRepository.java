package com.ChicaemeSAS.BackendSistema.repository;

import com.ChicaemeSAS.BackendSistema.model.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ESTA ES LA LÍNEA QUE FALTABA Y CAUSABA EL ERROR:
import java.util.List;

@Repository
public interface CotizacionesRepository extends JpaRepository<Cotizacion, Long> {

    // Este método permite buscar todas las cotizaciones de un usuario específico
    List<Cotizacion> findByUsuario_Id(Long usuarioId);
}