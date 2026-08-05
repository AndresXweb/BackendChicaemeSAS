package com.ChicaemeSAS.BackendSistema.controller;

import com.ChicaemeSAS.BackendSistema.model.servicios; // Punto y coma agregado
import com.ChicaemeSAS.BackendSistema.service.ServiciosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServiciosController {

    @Autowired
    private ServiciosService service;

    @GetMapping
    public List<servicios> obtenerServicios() {
        return service.obtenerServicios();
    }

    @PostMapping
    public servicios crearServicios(@RequestBody servicios servicios) {
        return service.guardarServicios(servicios);
    }

    @GetMapping("/{id}")
    public servicios obtenerServiciosPorId(@PathVariable Long id) { // Cambié el nombre para no chocar con el de arriba
        return service.obtenerPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminarServiciosPorId(@PathVariable Long id) {
        service.eliminarPorId(id);
    }

    @PutMapping("/{id}")
    public servicios actualizarServicios(@PathVariable Long id, @RequestBody servicios creandoServicios) {
        // Le seteamos el ID de la URL al objeto antes de pasarlo al servicio
        creandoServicios.setId(id);
        return service.guardarServicios(creandoServicios);
    }
}