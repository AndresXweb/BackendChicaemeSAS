package com.ChicaemeSAS.BackendSistema.service;
import com.ChicaemeSAS.BackendSistema.model.Empleados;
import com.ChicaemeSAS.BackendSistema.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class EmpleadosService {
    @Autowired
    private EmpleadoRepository empleadoRepository;

    public Empleados crearEmpleado(Empleados creandoEmpleado){
        return empleadoRepository.save(creandoEmpleado);
    }

    public List<Empleados> obtenerEmpleados(){
        return empleadoRepository.findAll();
    }

    public Empleados obtenerEmpleadoPorId(Long id){
        return empleadoRepository.findById(id).orElse(null);
    }

    public Empleados actualizarEmpleados(Long id, Empleados empleadosNuevos) {
        return empleadoRepository.findById(id).map(EmpleadosExistentes ->{
            EmpleadosExistentes.setNombres(empleadosNuevos.getNombres());
            EmpleadosExistentes.setApellidos(empleadosNuevos.getApellidos());
            EmpleadosExistentes.setDireccion(empleadosNuevos.getDireccion());
            EmpleadosExistentes.setEmail(empleadosNuevos.getEmail());
            EmpleadosExistentes.setTelefono(empleadosNuevos.getTelefono());
            EmpleadosExistentes.setRol(empleadosNuevos.getRol());
            EmpleadosExistentes.setArea(empleadosNuevos.getArea());
            return empleadoRepository.save(EmpleadosExistentes);
                }).orElse(null);
    }

    public void eliminarEmpleadoPorid(Long id){
        empleadoRepository.deleteById(id);
    }
}
