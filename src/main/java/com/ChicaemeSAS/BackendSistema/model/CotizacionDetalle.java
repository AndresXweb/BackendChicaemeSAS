package com.ChicaemeSAS.BackendSistema.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cotizacion_detalle")
public class CotizacionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. ¿A qué cotización pertenece este detalle? (La Cabecera)
    @ManyToOne
    @JoinColumn(name = "cotizacion_id", nullable = false)
    private Cotizacion cotizacion;

    // 2. Opción A: ¿El cliente está alquilando un objeto físico? (Puede ser nulo si es un servicio)
    @ManyToOne
    @JoinColumn(name = "articulo_id")
    private ArticuloAlquiler articuloAlquiler;

    // 3. Opción B: ¿El cliente está contratando comida o logística? (Puede ser nulo si es un artículo)
    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private servicios servicio; // Dejo "servicios" en minúscula tal como lo tienes actualmente

    // 4. Los números de la operación
    @Column(nullable = false)
    private Integer cantidad;

    // IMPORTANTE: Guardamos el precio al momento de la venta.
    // Si mañana cambias el precio en el catálogo, esta cotización antigua no debe alterarse.
    @Column(nullable = false)
    private Double precioUnitario;

    @Column(nullable = false)
    private Double subtotal; // cantidad * precioUnitario
}