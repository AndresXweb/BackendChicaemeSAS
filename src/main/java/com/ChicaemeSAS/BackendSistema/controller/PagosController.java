package com.ChicaemeSAS.BackendSistema.controller;
import com.ChicaemeSAS.BackendSistema.model.Pago;
import com.ChicaemeSAS.BackendSistema.service.PagoService;
import com.ChicaemeSAS.BackendSistema.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")

public class PagosController {
    @Autowired
    private PagoService pagoService;

    @GetMapping
    public List<Pago> findAll() {
        return pagoService.obtenerTodos();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pagoService.eliminarPorId(id);
    }

    @GetMapping("/{id}")
    public Pago buscarporId(@PathVariable Long id) {
        return pagoService.obtenerPorId(id);
    }

    @PostMapping
    public Pago agregarPago(@RequestBody Pago agregandoPago) {
        return pagoService.crearEmpleados(agregandoPago);
    }

     @PutMapping("/{id}")
    public Pago editarPago (@PathVariable Long id, @RequestBody Pago editandoPago) {
        return pagoService.actualizarPago(id, editandoPago);
     }
}
