package com.proyectosso.demo.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa a un profesor del sistema.
 *
 * Contiene los datos basicos del profesor y el identificador
 * asociado al usuario en Keycloak.
 */
@Entity
@Table(name = "profesores")
@Data
public class Profesor {
    /**
     * Identificador interno del profesor en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre completo del profesor.
     */
    private String nombre;

    /**
     * Correo electronico del profesor.
     */
    private String email;

    /**
     * Identificador unico del profesor en Keycloak.
     * Se utiliza para vincular el usuario autenticado con el registro de profesor.
     */
    @Column(name = "keycloak_id", unique = true)
    private String keycloakId;
}
