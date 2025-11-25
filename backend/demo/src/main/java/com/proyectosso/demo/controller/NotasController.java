package com.proyectosso.demo.controller;

import com.proyectosso.demo.model.Alumno;
import com.proyectosso.demo.model.Nota;
import com.proyectosso.demo.model.Profesor;
import com.proyectosso.demo.repository.AlumnoRepository;
import com.proyectosso.demo.repository.NotaRepository;
import com.proyectosso.demo.repository.ProfesorRepository;
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

/**
 * Controlador REST para la gestion de notas y relaciones entre alumnos y profesores.
 *
 * Expone endpoints para:
 * <ul>
 *     <li>Permitir que un alumno consulte sus propias calificaciones.</li>
 *     <li>Permitir que un profesor consulte la lista de sus alumnos y sus calificaciones.</li>
 * </ul>
 *
 * Los endpoints estan protegidos mediante roles definidos en Keycloak:
 * <ul>
 *     <li>Rol "alumno" para el acceso a las calificaciones propias.</li>
 *     <li>Rol "docente" para el acceso a la vista de alumnos de un profesor.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "https://midominio.lab"})
public class NotasController {

    /**
     * Repositorio para operaciones CRUD y consultas sobre notas.
     */
    @Autowired
    private NotaRepository notaRepo;

    /**
     * Repositorio para operaciones CRUD y consultas sobre alumnos.
     */
    @Autowired
    private AlumnoRepository alumnoRepo;

    /**
     * Repositorio para operaciones CRUD y consultas sobre profesores.
     */
    @Autowired
    private ProfesorRepository profesorRepo;

    /**
     * Obtiene las calificaciones del alumno actualmente autenticado.
     *
     * El alumno se identifica a partir del {@code keycloakId} contenido en el token JWT.
     * Si el alumno no esta registrado en la base de datos, se devuelve un error HTTP 404.
     *
     * El resultado se devuelve como una lista de mapas con la siguiente informacion:
     * <ul>
     *     <li>{@code id}: identificador de la nota.</li>
     *     <li>{@code materia}: nombre de la materia.</li>
     *     <li>{@code profesor}: nombre del profesor que imparte la materia.</li>
     *     <li>{@code calificacion}: calificacion obtenida.</li>
     * </ul>
     *
     * @param token token JWT del usuario autenticado, proporcionado por Spring Security
     * @return lista de mapas representando las notas del alumno autenticado
     */
    @GetMapping("/alumno/mis-calificaciones")
    @PreAuthorize("hasRole('alumno')")
    public List<Map<String, Object>> misNotas(@AuthenticationPrincipal Jwt token) {
        String keycloakId = token.getSubject();

        Alumno alumno = alumnoRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Alumno no registrado. Contacte al Admin."
                ));

        List<Nota> notas = notaRepo.findByAlumnoId(alumno.getId());

        return notas.stream().map(nota -> Map.<String, Object>of(
                "id", nota.getId(),
                "materia", nota.getMateria().getNombre(),
                "profesor", nota.getMateria().getProfesor().getNombre(),
                "calificacion", nota.getCalificacion()
        )).collect(Collectors.toList());
    }

    /**
     * Obtiene la lista de alumnos y sus calificaciones para el profesor autenticado.
     *
     * El profesor se identifica a partir del {@code keycloakId} contenido en el token JWT.
     * Si el profesor no existe en la base de datos, se devuelve un error HTTP 404.
     *
     * El resultado se devuelve como un mapa con la siguiente estructura:
     * <ul>
     *     <li>{@code materiaImpartida}: nombre de la materia impartida por el profesor
     *         (o "Sin Asignacion" si no hay notas registradas).</li>
     *     <li>{@code alumnos}: lista de mapas, uno por alumno, con:
     *         <ul>
     *             <li>{@code id}: identificador del alumno.</li>
     *             <li>{@code nombre}: nombre del alumno.</li>
     *             <li>{@code calificacion}: calificacion del alumno.</li>
     *             <li>{@code aprobado}: valor booleano que indica si la calificacion es
     *                 mayor o igual a 6.0.</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param token token JWT del usuario autenticado, proporcionado por Spring Security
     * @return mapa con el nombre de la materia impartida y la lista de alumnos con sus calificaciones
     */
    @GetMapping("/profesor/mis-alumnos")
    @PreAuthorize("hasRole('docente')")
    public Map<String, Object> misAlumnos(@AuthenticationPrincipal Jwt token) {
        String keycloakId = token.getSubject();

        Profesor profe = profesorRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profesor no encontrado."
                ));

        List<Nota> notasGrupo = notaRepo.findByMateriaProfesorId(profe.getId());

        String nombreMateria = notasGrupo.isEmpty()
                ? "Sin Asignacion"
                : notasGrupo.get(0).getMateria().getNombre();

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
