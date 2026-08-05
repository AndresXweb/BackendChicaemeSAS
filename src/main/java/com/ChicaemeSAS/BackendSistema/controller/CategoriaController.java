package com.ChicaemeSAS.BackendSistema.controller;
import com.ChicaemeSAS.BackendSistema.model.Categoria;
import com.ChicaemeSAS.BackendSistema.repository.CotizacionesRepository;
import com.ChicaemeSAS.BackendSistema.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaService.encontrarTodasCategorias();
    }

    @GetMapping("/{id}")
    public Categoria encontrarCategoriaPorId(@PathVariable Long id) {
        return categoriaService.encontrarCategoriaPorId(id);
    }

    @PostMapping
    public Categoria guardarCategoria(@RequestBody Categoria nuevaCategoria) {
        return categoriaService.guardarCategoria(nuevaCategoria);
    }
    @DeleteMapping("/{id}")
    public void eliminarCategoriaPorId(@PathVariable Long id) {
        categoriaService.eliminarCategoriaPorid(id);
    }

    @PutMapping("/{id}")
    public Categoria actualizarCategoria(@PathVariable Long id, @RequestBody Categoria actualizandoCategoria){
        return categoriaService.actualizarCategoria(id, actualizandoCategoria);
    }

}
