package com.ChicaemeSAS.BackendSistema.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactosDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    private String tipoContacto; // Opcional

    private String asunto; // Opcional

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    private Long usuarioId; // Opcional - NULL si no está logueado
}