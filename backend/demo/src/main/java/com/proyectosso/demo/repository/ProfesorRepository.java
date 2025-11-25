package com.proyectosso.demo.repository;

import com.proyectosso.demo.model.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA para la gestion de entidades {@link Profesor}.
 *
 * Proporciona operaciones CRUD basicas y consultas adicionales por keycloakId.
 */
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

    /**
     * Busca un profesor a partir de su identificador en Keycloak.
     *
     * @param keycloakId identificador unico del profesor en Keycloak
     * @return un Optional con el profesor encontrado, o vacio si no existe
     */
    Optional<Profesor> findByKeycloakId(String keycloakId);
}
