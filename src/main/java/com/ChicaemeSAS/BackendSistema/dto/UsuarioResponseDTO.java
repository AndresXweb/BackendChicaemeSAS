package com.ChicaemeSAS.BackendSistema.dto;

import com.ChicaemeSAS.BackendSistema.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Version "segura" del Usuario: mismos datos, SIN password.
// Esto es lo unico que el backend debe devolver al frontend a partir de ahora.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String nombres;
    private String apellidos;
    private String direccion;
    private String ciudad;
    private String telefono;
    private String email;
    private String tipoUsuario;
    private String imagen;

    // Convierte la entidad Usuario (que si tiene password) en este DTO seguro.
    public static UsuarioResponseDTO fromUsuario(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getDireccion(),
                usuario.getCiudad(),
                usuario.getTelefono(),
                usuario.getEmail(),
                usuario.getTipoUsuario(),
                usuario.getImagen()
        );
    }
}
