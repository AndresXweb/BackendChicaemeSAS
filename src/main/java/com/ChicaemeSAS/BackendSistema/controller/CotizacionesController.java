package com.ChicaemeSAS.BackendSistema.controller;

import com.ChicaemeSAS.BackendSistema.model.Cotizacion;
import com.ChicaemeSAS.BackendSistema.service.CotizacionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionesController {

    @Autowired
    private CotizacionesService cotizacionesService;

    @GetMapping
    public List<Cotizacion> obtenerCotizaciones() {
        return cotizacionesService.findAll();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Cotizacion> getCotizacionesByUsuario(@PathVariable Long usuarioId) {
        return cotizacionesService.obtenerCotizacionesPorUsuario(usuarioId);
    }

    @GetMapping("/{id}")
    public Cotizacion getCotizacionById(@PathVariable Long id) {
        return cotizacionesService.buscarCotizacionPorId(id);
    }

    @PostMapping
    public Cotizacion enviarCotizacion(@RequestBody Cotizacion creandoCotizacion) {
        return cotizacionesService.guardarCotizacion(creandoCotizacion);
    }

    // --- NUEVO ENDPOINT UNIVERSAL DE ESTADOS ---
    // Reemplaza al antiguo "/aprobar". Ahora recibe dinámicamente si es "Aprobada", "Rechazada", "Finalizado", etc.
    @PutMapping("/{id}/estado")
    public Cotizacion cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return cotizacionesService.cambiarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    public void eliminarCotizacion(@PathVariable Long id) {
        cotizacionesService.eliminarCotizacionPorId(id);
    }

    @PutMapping("/{id}")
    public Cotizacion actualizarCotizacion(@PathVariable Long id, @RequestBody Cotizacion cotizacion) {
        return cotizacionesService.actualizarCotizacion(id, cotizacion);
    }
}