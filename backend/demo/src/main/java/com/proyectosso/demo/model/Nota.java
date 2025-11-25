package com.proyectosso.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa una nota o calificacion asignada
 * a un alumno en una materia especifica.
 */
@Entity
@Table(name = "notas")
@Data
public class Nota {
    /**
     * Identificador interno de la nota en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Calificacion obtenida por el alumno en la materia.
     */
    private BigDecimal calificacion;
    
    /**
     * Fecha en la que se registro la nota.
     */
    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    /**
     * Alumno al que pertenece la nota.
     */
    @ManyToOne
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    /**
     * Materia en la cual se registro la nota.
     */
    @ManyToOne
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;
}
