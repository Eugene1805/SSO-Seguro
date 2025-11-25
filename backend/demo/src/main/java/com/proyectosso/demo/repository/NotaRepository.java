package com.proyectosso.demo.repository;

import com.proyectosso.demo.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la gestion de entidades {@link Nota}.
 *
 * Incluye metodos de consulta para obtener notas por alumno y por profesor.
 */
public interface NotaRepository extends JpaRepository<Nota, Long> {

    /**
     * Obtiene todas las notas asociadas a un alumno especifico.
     *
     * @param alumnoId identificador del alumno
     * @return lista de notas del alumno
     */
    List<Nota> findByAlumnoId(Long alumnoId);

    /**
     * Obtiene todas las notas de materias impartidas por un profesor especifico.
     *
     * @param profesorId identificador del profesor
     * @return lista de notas relacionadas con las materias del profesor
     */
    List<Nota> findByMateriaProfesorId(Long profesorId);
}
