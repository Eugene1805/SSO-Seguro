package com.proyectosso.demo.repository;

import com.proyectosso.demo.model.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    Optional<Profesor> findByKeycloakId(String keycloakId);
}