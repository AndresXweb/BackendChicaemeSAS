package com.ChicaemeSAS.BackendSistema.service;
import com.ChicaemeSAS.BackendSistema.model.Categoria;
import com.ChicaemeSAS.BackendSistema.repository.CategoriaRepository;
import com.ChicaemeSAS.BackendSistema.repository.CotizacionesRepository;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> encontrarTodasCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria encontrarCategoriaPorId(Long id){
        return categoriaRepository.findById(id).orElse(null);
    }

    public void eliminarCategoriaPorid(Long id){
        categoriaRepository.deleteById(id);
    }

    public Categoria guardarCategoria(Categoria guardandoCategoria){
        return categoriaRepository.save(guardandoCategoria);
    }

    public Categoria actualizarCategoria(Long id, Categoria nuevaCategoria){
        return categoriaRepository.findById(id).map(categoriaExistente -> {
            categoriaExistente.setNombreCategoria(nuevaCategoria.getNombreCategoria());
            categoriaExistente.setDescripcionCategoria(nuevaCategoria.getDescripcionCategoria());
        return categoriaRepository.save(categoriaExistente);
        }).orElse(null);
    }
}
