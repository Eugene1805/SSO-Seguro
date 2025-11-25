package com.proyectosso.demo.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa una materia impartida por un profesor.
 */
@Entity
@Table(name = "materias")
@Data
public class Materia {
    /**
     * Identificador interno de la materia en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre descriptivo de la materia.
     */
    private String nombre;

    /**
     * Profesor responsable de impartir la materia.
     */
    @ManyToOne
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;
}
