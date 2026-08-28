package com.ChicaemeSAS.BackendSistema.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Lo que manda el frontend cuando el usuario se autentica con el boton de Google.
//
// idToken: el JWT firmado por Google que el backend debe verificar contra las
//          llaves publicas de Google (nunca confiamos en datos de usuario que
//          vengan sueltos en el body, solo en lo que sale de verificar este token).
//
// aceptoTerminos: solo tiene efecto cuando no existe una cuenta previa con ese
//          correo (flujo de registro). Si la cuenta ya existe, este campo se ignora.
@Data
public class GoogleLoginRequest {

    @NotBlank
    private String idToken;

    private Boolean aceptoTerminos;
}
