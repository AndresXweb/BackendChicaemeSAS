package com.ChicaemeSAS.BackendSistema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lo que devuelve /api/usuarios/login cuando las credenciales son correctas.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private UsuarioResponseDTO usuario;
}
