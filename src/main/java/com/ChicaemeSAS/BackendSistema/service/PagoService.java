package com.ChicaemeSAS.BackendSistema.service;
import com.ChicaemeSAS.BackendSistema.model.Empleados;
import com.ChicaemeSAS.BackendSistema.model.Pago;
import com.ChicaemeSAS.BackendSistema.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class PagoService {
    @Autowired
    private PagoRepository pagoRepository;

    public List<Pago> obtenerTodos() {
        return pagoRepository.findAll();
    }

    public Pago crearEmpleados(Pago creandoPago){
        return pagoRepository.save(creandoPago);
    }

    public void eliminarPorId(Long id){
        pagoRepository.deleteById(id);
    }

    public Pago obtenerPorId(Long id){
        return pagoRepository.findById(id).orElse(null);
    }

    public Pago actualizarPago(Long id, Pago creandoPago){
        return pagoRepository.findById(id).map(pagosExistentes->{
            pagosExistentes.setFehcaPago(creandoPago.getFehcaPago());
            pagosExistentes.setMonto(creandoPago.getMonto());
            pagosExistentes.setMetodoPago(creandoPago.getMetodoPago());
            pagosExistentes.setComprobantePago(creandoPago.getComprobantePago());
            pagosExistentes.setCotizacion(creandoPago.getCotizacion());
            return pagoRepository.save(pagosExistentes);
        }).orElse(null);
    }
}