package com.proyectosso.demo.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa a un alumno del sistema.
 *
 * Contiene los datos basicos de identificacion y contacto, asi como
 * el identificador asociado al usuario en Keycloak.
 */
@Entity
@Table(name = "alumnos")
@Data
public class Alumno {
    /**
     * Identificador interno del alumno en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre completo del alumno.
     */
    private String nombre;

    /**
     * Correo electronico del alumno.
     */
    private String email;

    /**
     * Identificador unico del alumno en Keycloak.
     * Se utiliza para vincular el usuario autenticado con el registro de alumno.
     */
    @Column(name = "keycloak_id", unique = true)
    private String keycloakId;
}
