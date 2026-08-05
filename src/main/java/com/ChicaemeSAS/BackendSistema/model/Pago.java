package com.ChicaemeSAS.BackendSistema.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = false)

@Table (name = "pago")
public class Pago {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column (nullable = false)
    private LocalDate fehcaPago;

    @Column (nullable = false)
    private Double monto;

    @Column (nullable = false, length = 100)
    private String metodoPago;

    @Column (nullable = false, length = 100)
    private String comprobantePago;

    // --- LA EXPLICACIÓN DEL MANY TO ONE ---
    // Le decimos: "Muchos pagos pertenecen a Una Cotizacion"
    @ManyToOne
    // Le indicamos que en MySQL cree una columna llamada "cotizacion_id"
    @JoinColumn(name = "cotizacion_id", nullable = false)
    // Traemos el objeto COMPLETO de la cotización, no un simple String
    private Cotizacion cotizacion;
}
