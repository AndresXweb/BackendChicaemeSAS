package com.ChicaemeSAS.BackendSistema.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

// Verifica el idToken que manda el boton de Google contra las llaves publicas de Google
// (libreria oficial, nosotros no reimplementamos nada de la verificacion de firma).
//
// Importante: esto es lo unico en lo que confiamos para saber quien es el usuario.
// El controller nunca debe leer email/nombre directo del body de la peticion,
// solo del payload que devuelve este servicio despues de validar el token.
@Service
public class GoogleAuthService {

    @Value("${google.client.id}")
    private String googleClientId;

    private GoogleIdTokenVerifier verifier;

    private GoogleIdTokenVerifier getVerifier() {
        if (verifier == null) {
            verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
        }
        return verifier;
    }

    // Devuelve el payload verificado (email, nombre, apellido, si el correo esta
    // verificado por Google, etc.), o null si el token es invalido, esta expirado,
    // esta mal firmado, o fue emitido para un Client ID distinto al nuestro.
    public GoogleIdToken.Payload verificarToken(String idTokenString) {
        try {
            GoogleIdToken idToken = getVerifier().verify(idTokenString);
            return idToken != null ? idToken.getPayload() : null;
        } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
            return null;
        }
    }
}
