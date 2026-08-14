package com.ChicaemeSAS.BackendSistema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void enviarCorreoRecuperacion(String destinatario, String token) {
        String enlace = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Chicaeme SAS - Recuperación de contraseña");
        mensaje.setText(
                "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
                "Haz clic en el siguiente enlace para crear una nueva contraseña (válido por 30 minutos):\n" +
                enlace + "\n\n" +
                "Si tú no pediste esto, puedes ignorar este correo."
        );

        mailSender.send(mensaje);
    }
}
