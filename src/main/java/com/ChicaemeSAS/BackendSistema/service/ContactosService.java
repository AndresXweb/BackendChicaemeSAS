package com.ChicaemeSAS.BackendSistema.service;

import com.ChicaemeSAS.BackendSistema.dto.ContactosDTO;
import com.ChicaemeSAS.BackendSistema.model.Contactos;
import com.ChicaemeSAS.BackendSistema.model.Usuario;
import com.ChicaemeSAS.BackendSistema.repository.ContactosRepository;
import com.ChicaemeSAS.BackendSistema.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ContactosService {

    @Autowired
    private ContactosRepository contactosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Crear un nuevo contacto
     * @param contactosDTO Datos del formulario
     * @return Contacto guardado
     */
    public Contactos crearContacto(ContactosDTO contactosDTO) {
        // Crear entidad
        Contactos contacto = new Contactos();

        // Asignar datos básicos
        contacto.setNombre(contactosDTO.getNombre());
        contacto.setTelefono(contactosDTO.getTelefono());
        contacto.setEmail(contactosDTO.getEmail());
        contacto.setTipoContacto(contactosDTO.getTipoContacto());
        contacto.setAsunto(contactosDTO.getAsunto());
        contacto.setMensaje(contactosDTO.getMensaje());

        // Si viene usuario_id, asignar usuario
        if (contactosDTO.getUsuarioId() != null) {
            Optional<Usuario> usuario = usuarioRepository.findById(contactosDTO.getUsuarioId());
            usuario.ifPresent(contacto::setUsuario);
        }

        // Guardar
        Contactos contactoGuardado = contactosRepository.save(contacto);

        // TODO: Aquí podrías enviar email al admin
        // enviarEmailAlAdmin(contactoGuardado);

        return contactoGuardado;
    }

    /**
     * Obtener todos los contactos
     */
    public List<Contactos> obtenerTodos() {
        return contactosRepository.findAll();
    }

    /**
     * Obtener contacto por ID
     */
    public Optional<Contactos> obtenerPorId(Long id) {
        return contactosRepository.findById(id);
    }

    /**
     * Obtener contactos de un usuario específico
     */
    public List<Contactos> obtenerPorUsuario(Long usuarioId) {
        return contactosRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Obtener contactos por tipo
     */
    public List<Contactos> obtenerPorTipo(String tipo) {
        return contactosRepository.findByTipoContacto(tipo);
    }

    /**
     * Obtener contactos por email
     */
    public List<Contactos> obtenerPorEmail(String email) {
        return contactosRepository.findByEmail(email);
    }

    /**
     * Eliminar contacto
     */
    public void eliminarContacto(Long id) {
        contactosRepository.deleteById(id);
    }

    /**
     * TODO: Implementar envío de email al admin
     * private void enviarEmailAlAdmin(Contactos contacto) {
     *     // Usar JavaMailSender para enviar email
     *     String asunto = "Nuevo contacto de " + contacto.getNombre();
     *     String mensaje = "Has recibido un nuevo contacto...";
     *     // emailService.enviarEmail("admin@chicaeme.com", asunto, mensaje);
     * }
     */
}