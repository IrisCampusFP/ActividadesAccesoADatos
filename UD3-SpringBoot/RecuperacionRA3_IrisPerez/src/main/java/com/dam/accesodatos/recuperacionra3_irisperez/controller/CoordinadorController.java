package com.dam.accesodatos.recuperacionra3_irisperez.controller;

import com.dam.accesodatos.recuperacionra3_irisperez.DTO.AsignacionDTO;
import com.dam.accesodatos.recuperacionra3_irisperez.DTO.CamionDTO;
import com.dam.accesodatos.recuperacionra3_irisperez.DTO.RutaDTO;
import com.dam.accesodatos.recuperacionra3_irisperez.service.AsignacionService;
import com.dam.accesodatos.recuperacionra3_irisperez.service.CamionService;
import com.dam.accesodatos.recuperacionra3_irisperez.service.RutaService;
import com.dam.accesodatos.recuperacionra3_irisperez.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/coordinador")
public class CoordinadorController {

    private final UsuarioService usuarioService;
    private final CamionService camionService;
    private final RutaService rutaService;
    private final AsignacionService asignacionService;

    @Autowired
    public CoordinadorController(UsuarioService usuarioService, CamionService camionService, RutaService rutaService,
            AsignacionService asignacionService) {
        this.usuarioService = usuarioService;
        this.camionService = camionService;
        this.rutaService = rutaService;
        this.asignacionService = asignacionService;
    }

    // ···················
    // CAMIONES
    // ···················

    // Obtener todos los camiones
    @GetMapping("/camiones")
    public ResponseEntity<?> obtenerCamiones(HttpSession session) {
        usuarioService.comprobarCoordinador(session);
        List<CamionDTO> camiones = camionService.obtenerCamiones();
        return ResponseEntity.ok(camiones);
    }

    // ···················
    // RUTAS
    // ···················

    // Obtener todas las rutas
    @GetMapping("/rutas")
    public ResponseEntity<?> obtenerRutas(HttpSession session) {
        usuarioService.comprobarCoordinador(session);
        List<RutaDTO> rutas = rutaService.obtenerRutas();
        return ResponseEntity.ok(rutas);
    }

    // ···················
    // ASIGNACIONES
    // ···················

    // Obtener todas las asignaciones
    @GetMapping("/asignaciones")
    public ResponseEntity<?> obtenerAsignaciones(HttpSession session) {
        usuarioService.comprobarCoordinador(session);
        List<AsignacionDTO> asignaciones = asignacionService.obtenerAsignaciones();
        return ResponseEntity.ok(asignaciones);
    }

    // Crear una nueva asignación
    @PostMapping("/asignaciones")
    public ResponseEntity<?> crearAsignacion(@RequestBody Map<String, Long> body, HttpSession session) {
        usuarioService.comprobarCoordinador(session);
        try {
            Long camionId = body.get("camionId");
            Long rutaId = body.get("rutaId");
            AsignacionDTO nuevaAsignacion = asignacionService.crearAsignacion(camionId, rutaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignacion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("errorMsg", e.getMessage()));
        }
    }

    // Eliminar una asignación
    @DeleteMapping("/asignaciones/{id}")
    public ResponseEntity<?> eliminarAsignacion(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarCoordinador(session);
        asignacionService.eliminarAsignacion(id);
        return ResponseEntity.noContent().build();
    }
}
