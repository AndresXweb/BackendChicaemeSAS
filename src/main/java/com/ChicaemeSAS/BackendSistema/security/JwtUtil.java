package com.ChicaemeSAS.BackendSistema.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // TODO: mover esto a application.properties como "jwt.secret" (variable de entorno en produccion).
    // Debe tener al menos 32 caracteres para HS256. Este es solo un valor por defecto para arrancar.
    @Value("${jwt.secret:ChicaemeSAS_Clave_Secreta_Super_Larga_Para_Firmar_Tokens_JWT_2026}")
    private String secretKeyString;

    // 24 horas por defecto (en milisegundos). Tambien configurable via application.properties -> jwt.expiration
    @Value("${jwt.expiration:86400000}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // Genera un token con el email como "subject" y el rol (tipoUsuario) como claim extra
    public String generarToken(String email, String rol) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(getSigningKey())
                .compact();
    }

    public String extraerEmail(String token) {
        return extraerClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    public boolean esTokenValido(String token, String email) {
        try {
            String emailDelToken = extraerEmail(token);
            return emailDelToken.equals(email) && !estaExpirado(token);
        } catch (Exception e) {
            // Token corrupto, mal firmado o mal formado
            return false;
        }
    }

    private boolean estaExpirado(String token) {
        return extraerClaims(token).getExpiration().before(new Date());
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
