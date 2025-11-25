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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "https://midominio.lab"}) 
public class NotasController {

    @Autowired private NotaRepository notaRepo;
    @Autowired private AlumnoRepository alumnoRepo;
    @Autowired private ProfesorRepository profesorRepo;

    // --- VISTA ALUMNO ---
    @GetMapping("/alumno/mis-calificaciones")
    @PreAuthorize("hasRole('alumno')")
    public List<Map<String, Object>> misNotas(@AuthenticationPrincipal Jwt token) {
        String keycloakId = token.getSubject();
        
        Alumno alumno = alumnoRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no registrado. Contacte al Admin."));

        List<Nota> notas = notaRepo.findByAlumnoId(alumno.getId());

        // FIX: Usamos Map.<String, Object>of(...) para evitar errores de tipo
        return notas.stream().map(nota -> Map.<String, Object>of(
            "id", nota.getId(),
            "materia", nota.getMateria().getNombre(),
            "profesor", nota.getMateria().getProfesor().getNombre(),
            "calificacion", nota.getCalificacion()
        )).collect(Collectors.toList());
    }

    // --- VISTA DOCENTE ---
    @GetMapping("/profesor/mis-alumnos")
    @PreAuthorize("hasRole('docente')")
    public Map<String, Object> misAlumnos(@AuthenticationPrincipal Jwt token) {
        String keycloakId = token.getSubject();
        
        Profesor profe = profesorRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado."));
        
        List<Nota> notasGrupo = notaRepo.findByMateriaProfesorId(profe.getId());
        
        String nombreMateria = notasGrupo.isEmpty() ? "Sin Asignación" : notasGrupo.get(0).getMateria().getNombre();

        // FIX: Aquí estaba el error. Agregamos <String, Object> antes de .of
        List<Map<String, Object>> listaAlumnos = notasGrupo.stream().map(n -> Map.<String, Object>of(
            "id", n.getAlumno().getId(),
            "nombre", n.getAlumno().getNombre(),
            "calificacion", n.getCalificacion(),
            "aprobado", n.getCalificacion().doubleValue() >= 6.0
        )).collect(Collectors.toList());

        return Map.of(
            "materiaImpartida", nombreMateria,
            "alumnos", listaAlumnos
        );
    }
}