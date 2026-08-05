package com.ChicaemeSAS.BackendSistema.controller;

import com.ChicaemeSAS.BackendSistema.dto.ContactosDTO;
import com.ChicaemeSAS.BackendSistema.model.Contactos;
import com.ChicaemeSAS.BackendSistema.service.ContactosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/contactos")
@CrossOrigin(origins = "http://localhost:3000")
public class ContactosController {

    @Autowired
    private ContactosService contactosService;

    /**
     * POST - Crear un nuevo contacto
     * Accesible sin autenticación
     */
    @PostMapping
    public ResponseEntity<?> crearContacto(@Valid @RequestBody ContactosDTO contactosDTO) {
        try {
            Contactos contactoGuardado = contactosService.crearContacto(contactosDTO);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("mensaje", "Contacto enviado correctamente");
            respuesta.put("contacto", contactoGuardado);

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * GET - Obtener todos los contactos (solo admin)
     */
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<Contactos> contactos = contactosService.obtenerTodos();

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("total", contactos.size());
            respuesta.put("contactos", contactos);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET - Obtener contacto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Optional<Contactos> contacto = contactosService.obtenerPorId(id);

            if (contacto.isPresent()) {
                Map<String, Object> respuesta = new HashMap<>();
                respuesta.put("success", true);
                respuesta.put("contacto", contacto.get());

                return ResponseEntity.ok(respuesta);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("success", "false");
                error.put("error", "Contacto no encontrado");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET - Obtener contactos de un usuario específico
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<Contactos> contactos = contactosService.obtenerPorUsuario(usuarioId);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("total", contactos.size());
            respuesta.put("contactos", contactos);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET - Obtener contactos por tipo
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> obtenerPorTipo(@PathVariable String tipo) {
        try {
            List<Contactos> contactos = contactosService.obtenerPorTipo(tipo);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("total", contactos.size());
            respuesta.put("contactos", contactos);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * DELETE - Eliminar contacto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarContacto(@PathVariable Long id) {
        try {
            contactosService.eliminarContacto(id);

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("success", "true");
            respuesta.put("mensaje", "Contacto eliminado correctamente");

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}