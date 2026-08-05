package com.ChicaemeSAS.BackendSistema.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importante para evitar ciclos JSON
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList; // Para inicializar la lista vacía
import java.util.List;      // Para manejar la lista de detalles

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cotizaciones")
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EL TRUCO PARA POSTMAN: Le decimos exactamente cómo llegará el texto
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate fechaEvento;

    @Column(nullable = false)
    private Integer cantidadPersonas;

    @Column(nullable = false, length = 100)
    private String estado;

    @Column(nullable = false)
    private Double total;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // --- AQUÍ ESTÁ LA MAGIA NUEVA ---
    // mappedBy = "cotizacion" -> Busca el campo "cotizacion" en la clase CotizacionDetalle
    // cascade = CascadeType.ALL -> Si guardas o borras la Cotización, guarda o borra sus detalles
    // orphanRemoval = true -> Si quitas un detalle de la lista, se borra de la base de datos
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("cotizacion") // Evita un bucle infinito al convertir a JSON
    private List<CotizacionDetalle> detalles = new ArrayList<>();
}