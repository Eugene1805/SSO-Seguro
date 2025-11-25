package com.proyectosso.demo.repository;

import com.proyectosso.demo.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findByKeycloakId(String keycloakId);
}