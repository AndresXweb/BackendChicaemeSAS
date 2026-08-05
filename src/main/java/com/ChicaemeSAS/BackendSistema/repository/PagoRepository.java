package com.ChicaemeSAS.BackendSistema.repository;
import com.ChicaemeSAS.BackendSistema.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PagoRepository extends JpaRepository<Pago, Long> {

}
