package com.proyectosso.demo.controller;

import com.proyectosso.demo.model.*;
import com.proyectosso.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permite que React se conecte sin bloqueos
public class NotasController {

    @Autowired private NotaRepository notaRepo;
    @Autowired private AlumnoRepository alumnoRepo;
    @Autowired private ProfesorRepository profesorRepo;

    // --- Endpoint para ALUMNOS ---
    @GetMapping("/alumno/mis-calificaciones")
    @PreAuthorize("hasRole('alumno')")
    public List<Nota> misNotas(@AuthenticationPrincipal Jwt token) {
        String keycloakId = token.getSubject();
        Alumno alumno = alumnoRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado en BD"));
        return notaRepo.findByAlumnoId(alumno.getId());
    }

    // --- Endpoint para DOCENTES ---
    @GetMapping("/profesor/mis-alumnos")
    @PreAuthorize("hasRole('docente')")
    public Map<String, Object> misAlumnos(@AuthenticationPrincipal Jwt token) {
        String keycloakId = token.getSubject();
        Profesor profe = profesorRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado en BD"));
        
        List<Nota> notasGrupo = notaRepo.findByMateriaProfesorId(profe.getId());
        
        // Estructura simple para el Frontend
        return Map.of(
            "materiaImpartida", "Ciencias Computacionales", // Podrías sacarlo dinámicamente de la lista
            "alumnos", notasGrupo.stream().map(n -> Map.of(
                "id", n.getAlumno().getId(),
                "nombre", n.getAlumno().getNombre(),
                "calificacion", n.getCalificacion(),
                "aprobado", n.getCalificacion().doubleValue() >= 6.0
            )).toList()
        );
    }
}