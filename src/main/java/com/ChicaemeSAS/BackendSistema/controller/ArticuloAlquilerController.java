package com.ChicaemeSAS.BackendSistema.controller;

import com.ChicaemeSAS.BackendSistema.model.ArticuloAlquiler;
import com.ChicaemeSAS.BackendSistema.service.ArticuloAlquilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articulos") // Esta será la URL base
public class ArticuloAlquilerController {

    @Autowired
    private ArticuloAlquilerService service;

    // Petición GET: Para ver todos los artículos (http://localhost:8080/api/articulos)
    @GetMapping
    public List<ArticuloAlquiler> listarArticulos() {
        return service.obtenerTodos();
    }

    // Petición POST: Para crear un nuevo artículo
    @PostMapping
    public ArticuloAlquiler crearArticulo(@RequestBody ArticuloAlquiler articulo) {
        return service.guardarArticulo(articulo);
    }
    // Dentro de ArticuloAlquilerController.java

    @GetMapping("/{id}")
    public ArticuloAlquiler obtenerArticulo(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public ArticuloAlquiler actualizarArticulo(@PathVariable Long id, @RequestBody ArticuloAlquiler actualizandoArticulo) {
        return service.actualizarArticulo(id, actualizandoArticulo);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminarPorId(id);
    }
}