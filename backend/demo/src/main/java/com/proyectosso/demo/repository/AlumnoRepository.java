package com.proyectosso.demo.repository;

import com.proyectosso.demo.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA para la gestion de entidades {@link Alumno}.
 *
 * Proporciona operaciones CRUD basicas y consultas adicionales por keycloakId.
 */
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    /**
     * Busca un alumno a partir de su identificador en Keycloak.
     *
     * @param keycloakId identificador unico del alumno en Keycloak
     * @return un Optional con el alumno encontrado, o vacio si no existe
     */
    Optional<Alumno> findByKeycloakId(String keycloakId);
}
