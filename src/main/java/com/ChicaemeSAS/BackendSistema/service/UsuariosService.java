package com.ChicaemeSAS.BackendSistema.service;

import com.ChicaemeSAS.BackendSistema.model.Usuario;
import com.ChicaemeSAS.BackendSistema.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuariosService {

    private static final int PASSWORD_MIN_LENGTH = 8;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Validación puntual (no en el modelo): así no afecta las actualizaciones
    // que mandan password vacío a propósito para "no cambiarla".
    private void validarLongitudPassword(String password) {
        if (password == null || password.length() < PASSWORD_MIN_LENGTH) {
            throw new IllegalArgumentException("La contraseña debe tener al menos " + PASSWORD_MIN_LENGTH + " caracteres.");
        }
    }

    public List<Usuario> findAll() {
        return repository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        validarLongitudPassword(usuario.getPassword());
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Con el campo como Boolean (wrapper) ya puede llegar null sin romper el
        // JSON, pero la columna en la BD sigue siendo NOT NULL — así que si llega
        // vacío (ej: el panel admin no manda este campo), lo dejamos en false.
        if (usuario.getAceptoTerminos() == null) {
            usuario.setAceptoTerminos(false);
        }

        return repository.save(usuario);
    }

    public Usuario buscarUsuarioPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public Usuario buscarUsuarioPorTelefono(String telefono) {
        return repository.findByTelefono(telefono).orElse(null);
    }

    public void eliminarUsuarioPorId(Long id) {
        repository.deleteById(id);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioNuevo) {
        return repository.findById(id).map(usuarioExistente -> {
            usuarioExistente.setNombres(usuarioNuevo.getNombres());
            usuarioExistente.setApellidos(usuarioNuevo.getApellidos());
            usuarioExistente.setDireccion(usuarioNuevo.getDireccion());
            usuarioExistente.setCiudad(usuarioNuevo.getCiudad());
            usuarioExistente.setTelefono(usuarioNuevo.getTelefono());
            usuarioExistente.setEmail(usuarioNuevo.getEmail());

            // Solo re-hasheamos si mandaron una contraseña nueva.
            // Si el campo viene vacío/null, dejamos la contraseña actual sin tocar.
            if (usuarioNuevo.getPassword() != null && !usuarioNuevo.getPassword().isBlank()) {
                validarLongitudPassword(usuarioNuevo.getPassword());
                usuarioExistente.setPassword(passwordEncoder.encode(usuarioNuevo.getPassword()));
            }

            usuarioExistente.setTipoUsuario(usuarioNuevo.getTipoUsuario());
            usuarioExistente.setImagen(usuarioNuevo.getImagen());
            return repository.save(usuarioExistente);
        }).orElse(null);
    }

    // --- LOGIN: ahora compara con BCrypt en vez de .equals() ---
    public Usuario autenticarUsuario(String email, String password) {
        Usuario usuario = buscarUsuarioPorEmail(email);

        if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
            return usuario;
        }

        return null;
    }

    // --- RECUPERACIÓN DE CONTRASEÑA ---

    // Devuelve el token generado, o null si el correo no existe.
    // El controller decide qué responder al frontend (siempre el mismo mensaje, exista o no el correo).
    public String generarTokenRecuperacion(String email) {
        Usuario usuario = buscarUsuarioPorEmail(email);
        if (usuario == null) {
            return null;
        }

        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setResetTokenExpira(LocalDateTime.now().plusMinutes(30));
        repository.save(usuario);
        return token;
    }

    // Devuelve true si el token era válido y la contraseña quedó actualizada.
    public boolean restablecerPassword(String token, String nuevaPassword) {
        Usuario usuario = repository.findByResetToken(token).orElse(null);

        if (usuario == null || usuario.getResetTokenExpira() == null
                || usuario.getResetTokenExpira().isBefore(LocalDateTime.now())) {
            return false; // token inexistente, ya usado, o expirado
        }

        validarLongitudPassword(nuevaPassword);
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setResetToken(null);
        usuario.setResetTokenExpira(null);
        repository.save(usuario);
        return true;
    }

    // --- REGISTRO PÚBLICO ---
    // Regla de negocio compartida entre el formulario de registro normal y el
    // login/registro con Google: fuerza tipoUsuario = Cliente (nadie se auto-asigna
    // admin por ninguna de las dos puertas) y deja constancia de cuándo aceptó términos.
    // Antes esta regla vivía solo dentro de UsuarioController.crearUsuario(); se movió
    // aquí para que Google Sign-In no tenga que duplicarla.
    public Usuario registrarUsuarioPublico(Usuario nuevoUsuario, boolean aceptoTerminos) {
        if (!aceptoTerminos) {
            throw new IllegalArgumentException("Debes aceptar los términos y condiciones para registrarte.");
        }
        nuevoUsuario.setTipoUsuario("Cliente");
        nuevoUsuario.setAceptoTerminos(true);
        nuevoUsuario.setFechaAceptacionTerminos(LocalDateTime.now());
        return guardarUsuario(nuevoUsuario);
    }

    // --- LOGIN / REGISTRO CON GOOGLE ---
    // Si el correo ya existe en la BD (con cualquier rol: Cliente, Admin, Staff...),
    // simplemente logueamos esa cuenta — no se crea una segunda cuenta duplicada,
    // y el rol existente no se toca ni se pisa.
    // Si no existe, se crea una cuenta nueva forzada a Cliente (misma regla que el
    // registro manual), con una contraseña aleatoria: nadie la necesita para entrar
    // por Google, solo existe porque la columna password es NOT NULL. Si esa persona
    // más adelante quiere entrar también con correo+contraseña, usa "olvidé mi contraseña".
    public Usuario autenticarOCrearConGoogle(String email, String nombres, String apellidos, boolean aceptoTerminos) {
        Usuario existente = buscarUsuarioPorEmail(email);
        if (existente != null) {
            return existente;
        }

        Usuario nuevo = new Usuario();
        nuevo.setEmail(email);
        nuevo.setNombres(nombres != null ? nombres : "");
        nuevo.setApellidos(apellidos != null ? apellidos : "");
        nuevo.setPassword(UUID.randomUUID().toString()); // se re-hashea dentro de guardarUsuario()

        return registrarUsuarioPublico(nuevo, aceptoTerminos);
    }
}
