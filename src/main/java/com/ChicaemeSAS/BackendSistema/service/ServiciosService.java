package com.ChicaemeSAS.BackendSistema.service;

import com.ChicaemeSAS.BackendSistema.model.servicios;
import com.ChicaemeSAS.BackendSistema.repository.ServiciosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiciosService {

    @Autowired
    private ServiciosRepository repository; // Corregido a minúscula

    // Retorna 'servicios', no 'ServiciosService'
    public servicios guardarServicios(servicios servicios) {
        return repository.save(servicios);
    }

    public List<servicios> obtenerServicios() {
        return repository.findAll(); // Corregida la 'A' mayúscula
    }

    // Retorna 'servicios' y usamos 'Long' con mayúscula
    public servicios obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void eliminarPorId(long id) {
        repository.deleteById(id);
    }

    public servicios actualizarServicio(Long id, servicios datosNuevos) {
        return repository.findById(id).map(servicioExistente -> {
            // Usamos los nombres reales de la tabla 'servicios'
            servicioExistente.setNombre(datosNuevos.getNombre());
            servicioExistente.setDescripcion(datosNuevos.getDescripcion());
            servicioExistente.setPrecioBase(datosNuevos.getPrecioBase());
            servicioExistente.setUnidadMedida(datosNuevos.getUnidadMedida());
            servicioExistente.setImagen(datosNuevos.getImagen());
            return repository.save(servicioExistente);
        }).orElse(null);
    }
}