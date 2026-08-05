package com.ChicaemeSAS.BackendSistema.service;

import com.ChicaemeSAS.BackendSistema.model.ArticuloAlquiler;
import com.ChicaemeSAS.BackendSistema.repository.ArticuloAlquilerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticuloAlquilerService {

    @Autowired
    private ArticuloAlquilerRepository repository;

    // Método para guardar un nuevo artículo
    public ArticuloAlquiler guardarArticulo(ArticuloAlquiler articulo) {
        return repository.save(articulo);
    }

    // Método para traer todo el inventario
    public List<ArticuloAlquiler> obtenerTodos() {
        return repository.findAll();
    }

    // Método para buscar por ID
    public ArticuloAlquiler obtenerPorId(long id) {
        return repository.findById(id).orElse(null);
    }

    // Método para eliminar
    public void eliminarPorId(long id) {
        repository.deleteById(id);
    }

    // MÉTODO ACTUALIZADO: Ahora incluye fotoUrl
    public ArticuloAlquiler actualizarArticulo(Long id, ArticuloAlquiler datosNuevos) {
        return repository.findById(id).map(articuloExistente -> {
            articuloExistente.setNombre(datosNuevos.getNombre());
            articuloExistente.setDescripcion(datosNuevos.getDescripcion());
            articuloExistente.setPrecioAlquiler(datosNuevos.getPrecioAlquiler());
            articuloExistente.setStockTotal(datosNuevos.getStockTotal());

            // Línea clave agregada para manejar la foto
            articuloExistente.setFotoUrl(datosNuevos.getFotoUrl());

            return repository.save(articuloExistente);
        }).orElse(null);
    }
}