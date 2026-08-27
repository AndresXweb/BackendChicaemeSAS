package com.ChicaemeSAS.BackendSistema.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios") // La tabla se recomienda en minúscula plural
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO ESTO DEBE IR EN MINÚSCULA
    @NotBlank(message = "El nombre no puede ir vacio")
    @Column(nullable = false, length = 100)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios!")
    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, length = 100)
    private String direccion;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Email (message = "Debe ser un correo electronico valido")
    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    // En Java usamos camelCase (tipoUsuario)
    @Column(name = "tipo_usuario", nullable = false, length = 20)
    private String tipoUsuario;

    @Column(name = "imagen", length = 500) // Le damos 500 de longitud por si la URL es muy larga
    private String imagen;

    // --- Recuperación de contraseña ---
    @Column(name = "reset_token", length = 255)
    private String resetToken;

    @Column(name = "reset_token_expira")
    private LocalDateTime resetTokenExpira;

    // --- Términos y condiciones ---
    // columnDefinition con DEFAULT 0 para que la migración no falle sobre usuarios ya existentes
    @Column(name = "acepto_terminos", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean aceptoTerminos;

    @Column(name = "fecha_aceptacion_terminos")
    private LocalDateTime fechaAceptacionTerminos;

}