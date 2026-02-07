package com.dam.accesodatos.centromedicora3_irisperez.controller;

import com.dam.accesodatos.centromedicora3_irisperez.DTO.PacienteDTO;
import com.dam.accesodatos.centromedicora3_irisperez.service.PacienteService;
import com.dam.accesodatos.centromedicora3_irisperez.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recepcion")
public class RecepcionController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    PacienteService pacienteService;

    // Obtener todos los pacientes
    @GetMapping("/pacientes")
    public ResponseEntity<?> obtenerPacientes(HttpSession session) {
        usuarioService.comprobarRecepcion(session);
        List<PacienteDTO> pacientes = pacienteService.obtenerPacientes();
        return ResponseEntity.ok(pacientes);
    }
}

