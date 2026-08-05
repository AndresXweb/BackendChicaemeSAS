package com.ChicaemeSAS.BackendSistema.repository;
import com.ChicaemeSAS.BackendSistema.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CategoriaRepository extends  JpaRepository<Categoria,Long> {
}
