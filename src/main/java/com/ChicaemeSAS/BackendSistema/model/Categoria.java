package com.ChicaemeSAS.BackendSistema.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table (name = "categorias")
public class Categoria {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, length = 50)
    private String nombreCategoria;

    @Column (nullable = false, length = 200)
    private String descripcionCategoria;

}
