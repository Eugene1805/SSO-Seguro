package com.proyectosso.demo.repository;

import com.proyectosso.demo.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByAlumnoId(Long alumnoId);
    List<Nota> findByMateriaProfesorId(Long profesorId);
}