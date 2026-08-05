package com.ChicaemeSAS.BackendSistema.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name = "servicios")
public class servicios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String descripcion;

    @Column(nullable = false)
    private Double precioBase;

    @Column(nullable = false, length = 100)
    private String unidadMedida;

    @Column(name = "imagen", length = 500) // Le damos 500 de longitud por si la URL es muy larga
    private String imagen;
}
