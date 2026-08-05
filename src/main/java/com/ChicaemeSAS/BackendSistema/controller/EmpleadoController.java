package com.ChicaemeSAS.BackendSistema.controller;
import com.ChicaemeSAS.BackendSistema.model.Empleados;
import com.ChicaemeSAS.BackendSistema.repository.EmpleadoRepository;
import com.ChicaemeSAS.BackendSistema.service.EmpleadosService;
import com.ChicaemeSAS.BackendSistema.service.ServiciosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")

public class EmpleadoController {
    @Autowired
    private EmpleadosService empleadosService;

@PutMapping("/{id}")
    public Empleados actualizarEmpleados(@PathVariable Long id, @RequestBody Empleados actualizandoEmpleados){
    return empleadosService.actualizarEmpleados(id, actualizandoEmpleados);
}

@PostMapping
    public Empleados insetarEmpleados(@RequestBody Empleados empleadosNuevos){
    return empleadosService.crearEmpleado(empleadosNuevos);
}

@GetMapping
public List<Empleados> obtenerEmpleados(){
    return empleadosService.obtenerEmpleados();
}

@DeleteMapping("/{id}")
    public void eliminarEmpleadoPorId(@PathVariable Long id){
    empleadosService.eliminarEmpleadoPorid(id);
}

@GetMapping("/{id}")
    public Empleados buscarEmpleadoPorId(@PathVariable Long id){
    return empleadosService.obtenerEmpleadoPorId(id);
}

}
