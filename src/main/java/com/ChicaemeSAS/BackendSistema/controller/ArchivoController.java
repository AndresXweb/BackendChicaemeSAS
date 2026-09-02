package com.ChicaemeSAS.BackendSistema.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Un solo endpoint reutilizable: recibe cualquier foto (de usuario, artículo o
// servicio) y devuelve la URL donde quedó guardada. Los modelos (Usuario,
// ArticuloAlquiler, servicios) no cambian - siguen guardando un String URL,
// solo que ahora esa URL la genera este endpoint en vez de escribirla la persona.
@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${server.port:8080}")
    private String puerto;

    private static final List<String> TIPOS_PERMITIDOS = List.of("image/jpeg", "image/png", "image/webp");

    @PostMapping("/imagen")
    public ResponseEntity<?> subirImagen(@RequestParam("archivo") MultipartFile archivo) {

        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("No se recibió ningún archivo.");
        }
        if (!TIPOS_PERMITIDOS.contains(archivo.getContentType())) {
            return ResponseEntity.badRequest().body("Solo se permiten imágenes JPG, PNG o WEBP.");
        }

        try {
            File carpeta = new File(uploadDir);
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            String extension = switch (archivo.getContentType()) {
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                default -> ".jpg";
            };
            String nombreArchivo = UUID.randomUUID() + extension;

            Path destino = Path.of(uploadDir, nombreArchivo);
            Files.copy(archivo.getInputStream(), destino);

            String url = "http://localhost:" + puerto + "/uploads/" + nombreArchivo;
            return ResponseEntity.ok(Map.of("url", url));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo guardar la imagen.");
        }
    }
}
