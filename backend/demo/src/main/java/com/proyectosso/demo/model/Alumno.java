package com.proyectosso.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "alumnos")
@Data
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;

    @Column(name = "keycloak_id", unique = true)
    private String keycloakId;
}