package com.dam.accesodatos.recuperacionra3_irisperez.controller;

import com.dam.accesodatos.recuperacionra3_irisperez.DTO.CamionDTO;
import com.dam.accesodatos.recuperacionra3_irisperez.DTO.RutaDTO;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Camion;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Ruta;
import com.dam.accesodatos.recuperacionra3_irisperez.service.CamionService;
import com.dam.accesodatos.recuperacionra3_irisperez.service.RutaService;
import com.dam.accesodatos.recuperacionra3_irisperez.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recepcion")
public class CoordinadorController {


    UsuarioService usuarioService;
    CamionService camionService;
    RutaService rutaService;

    @Autowired
    public CoordinadorController(UsuarioService usuarioService, CamionService camionService, RutaService rutaService) {
        this.usuarioService = usuarioService;
        this.camionService = camionService;
        this.rutaService = rutaService;
    }

    // ···················
    //      CAMIONES
    // ···················

    // Obtener todos los camiones
    @GetMapping("/camiones")
    public ResponseEntity<?> obtenerCamiones(HttpSession session) {
        usuarioService.comprobarCoordinador(session);
        List<CamionDTO> camiones = camionService.obtenerCamiones();
        return ResponseEntity.ok(camiones);
    }

    // ···················
    //      RUTAS
    // ···················

    // Obtener todas las rutas
    @GetMapping("/rutas")
    public ResponseEntity<?> obtenerRutas(HttpSession session) {
        usuarioService.comprobarCoordinador(session);
        List<RutaDTO> rutas = rutaService.obtenerRutas();
        return ResponseEntity.ok(rutas);
    }
}

