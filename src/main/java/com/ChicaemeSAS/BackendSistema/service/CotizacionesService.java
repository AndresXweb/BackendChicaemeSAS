package com.ChicaemeSAS.BackendSistema.service;

import com.ChicaemeSAS.BackendSistema.model.Cotizacion;
import com.ChicaemeSAS.BackendSistema.model.CotizacionDetalle;
import com.ChicaemeSAS.BackendSistema.model.ArticuloAlquiler;
import com.ChicaemeSAS.BackendSistema.repository.CotizacionesRepository;
import com.ChicaemeSAS.BackendSistema.repository.ArticuloAlquilerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CotizacionesService {

    @Autowired
    private CotizacionesRepository cotizacionesRepository;

    @Autowired
    private ArticuloAlquilerRepository articuloRepository;

    public List<Cotizacion> findAll() { return cotizacionesRepository.findAll(); }

    public Cotizacion buscarCotizacionPorId(Long id) { return cotizacionesRepository.findById(id).orElse(null); }

    public List<Cotizacion> obtenerCotizacionesPorUsuario(Long usuarioId) { return cotizacionesRepository.findByUsuario_Id(usuarioId); }

    @Transactional
    public Cotizacion guardarCotizacion(Cotizacion cotizacion) {
        double totalCalculado = 0.0;
        cotizacion.setEstado("Pendiente");

        if (cotizacion.getDetalles() != null) {
            for (CotizacionDetalle detalle : cotizacion.getDetalles()) {
                detalle.setCotizacion(cotizacion);
                if (detalle.getCantidad() != null && detalle.getPrecioUnitario() != null) {
                    detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
                    totalCalculado += detalle.getSubtotal();
                }
            }
        }
        cotizacion.setTotal(totalCalculado);
        return cotizacionesRepository.save(cotizacion);
    }

    @Transactional
    public Cotizacion cambiarEstado(Long id, String nuevoEstado) {
        Cotizacion cotizacion = cotizacionesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        String estadoActual = cotizacion.getEstado();

        if (estadoActual.equals(nuevoEstado)) return cotizacion;

        if ("Aprobado".equals(estadoActual) && ("Finalizado".equals(nuevoEstado) || "Rechazado".equals(nuevoEstado) || "Pendiente".equals(nuevoEstado))) {
            devolverStock(cotizacion);
        }

        if ("Aprobado".equals(nuevoEstado) && !"Aprobado".equals(estadoActual)) {
            descontarStock(cotizacion);
        }

        cotizacion.setEstado(nuevoEstado);
        return cotizacionesRepository.save(cotizacion);
    }

    private void descontarStock(Cotizacion cotizacion) {
        for (CotizacionDetalle detalle : cotizacion.getDetalles()) {
            ArticuloAlquiler articulo = articuloRepository.findById(detalle.getArticuloAlquiler().getId()).orElseThrow();
            if (articulo.getStockTotal() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + articulo.getNombre());
            }
            articulo.setStockTotal(articulo.getStockTotal() - detalle.getCantidad());
            articuloRepository.save(articulo);
        }
    }

    private void devolverStock(Cotizacion cotizacion) {
        for (CotizacionDetalle detalle : cotizacion.getDetalles()) {
            ArticuloAlquiler articulo = articuloRepository.findById(detalle.getArticuloAlquiler().getId()).orElseThrow();
            articulo.setStockTotal(articulo.getStockTotal() + detalle.getCantidad());
            articuloRepository.save(articulo);
        }
    }

    @Transactional
    public void eliminarCotizacionPorId(Long id) {
        Cotizacion cotizacion = cotizacionesRepository.findById(id).orElse(null);
        if (cotizacion != null) {
            if ("Aprobado".equals(cotizacion.getEstado())) {
                devolverStock(cotizacion);
            }
            cotizacionesRepository.deleteById(id);
        }
    }

    // --- AQUÍ ESTÁ LA CORRECCIÓN CRÍTICA ---
    @Transactional
    public Cotizacion actualizarCotizacion(Long id, Cotizacion cotizacionNueva) {
        return cotizacionesRepository.findById(id).map(existente -> {
            // 1. Devolver stock si ya estaba aprobado
            if ("Aprobado".equals(existente.getEstado())) {
                devolverStock(existente);
            }

            // 2. Actualizar datos básicos
            existente.setCantidadPersonas(cotizacionNueva.getCantidadPersonas());
            existente.setFechaEvento(cotizacionNueva.getFechaEvento());
            existente.setEstado("Pendiente");

            // 3. ACTUALIZAR LA LISTA DE DETALLES
            if (cotizacionNueva.getDetalles() != null) {
                existente.getDetalles().clear(); // Borramos los productos viejos
                double nuevoTotal = 0.0;

                for (CotizacionDetalle nuevoDetalle : cotizacionNueva.getDetalles()) {
                    nuevoDetalle.setCotizacion(existente); // Asociamos al padre
                    nuevoDetalle.setSubtotal(nuevoDetalle.getCantidad() * nuevoDetalle.getPrecioUnitario());
                    nuevoTotal += nuevoDetalle.getSubtotal();
                    existente.getDetalles().add(nuevoDetalle); // Agregamos los nuevos
                }
                existente.setTotal(nuevoTotal); // Calculamos el nuevo total
            }

            return cotizacionesRepository.save(existente);
        }).orElse(null);
    }
}