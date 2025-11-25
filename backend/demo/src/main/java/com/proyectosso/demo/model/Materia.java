package com.proyectosso.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "materias")
@Data
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;
}