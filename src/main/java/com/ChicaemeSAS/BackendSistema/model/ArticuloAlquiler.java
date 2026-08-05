package com.ChicaemeSAS.BackendSistema.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "articulos_alquiler")
public class ArticuloAlquiler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Double precioAlquiler;

    @Column(nullable = false)
    private Integer stockTotal;

    // --- AQUÍ ESTÁ EL ESPACIO PARA LA FOTO ---
    // Guardamos la URL o el link de la imagen
    @Column(length = 500)
    private String fotoUrl;

}