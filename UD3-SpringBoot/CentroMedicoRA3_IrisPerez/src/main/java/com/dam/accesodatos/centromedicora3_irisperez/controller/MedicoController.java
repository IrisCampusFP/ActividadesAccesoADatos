package com.dam.accesodatos.centromedicora3_irisperez.controller;

import com.dam.accesodatos.centromedicora3_irisperez.DTO.PacienteDTO;
import com.dam.accesodatos.centromedicora3_irisperez.DTO.PacienteUpdateDTO;
import com.dam.accesodatos.centromedicora3_irisperez.DTO.UsuarioDTO;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Paciente;
import com.dam.accesodatos.centromedicora3_irisperez.service.PacienteService;
import com.dam.accesodatos.centromedicora3_irisperez.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medico")
public class MedicoController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    PacienteService pacienteService;

    // Crear un nuevo paciente
    @PostMapping("/pacientes")
    public ResponseEntity<?> crearPaciente(@RequestBody Paciente paciente, HttpSession session) {
        usuarioService.comprobarMedico(session);
        try {
            UsuarioDTO medico = (UsuarioDTO) session.getAttribute("usuarioDTO");
            PacienteDTO nuevoPaciente = pacienteService.crearPacienteMedico(paciente, medico.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPaciente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("errorMsg", e.getMessage()));
        }
    }

    // Obtener todos los pacientes
    @GetMapping("/pacientes")
    public ResponseEntity<?> obtenerPacientes(HttpSession session) {
        usuarioService.comprobarMedico(session);
        UsuarioDTO dtoMedico = (UsuarioDTO) session.getAttribute("usuarioDTO");
        List<PacienteDTO> pacientes = pacienteService.obtenerPacientesMedico(dtoMedico.getId());
        return ResponseEntity.ok(pacientes);
    }

    // Actualizar paciente
    // (Se utiliza PathVariable para asegurarnos de actualizar el paciente correspondiente)
    @PutMapping("/pacientes/{id}")
    public ResponseEntity<?> actualizarPaciente(@PathVariable Long id, @RequestBody PacienteUpdateDTO pacienteActualizado, HttpSession session) {
        usuarioService.comprobarMedico(session);

        // Se actualiza el paciente en la base de datos
        try {
            PacienteDTO paciente = pacienteService.actualizarPaciente(id, pacienteActualizado);
            return ResponseEntity.ok(paciente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("errorMsg", e.getMessage()));
        }
    }

    // Obtener paciente por ID
    @GetMapping("/pacientes/{id}")
    public ResponseEntity<?> obtenerPacientePorId(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarMedico(session);
        PacienteDTO paciente = pacienteService.obtenerPacientePorId(id);
        return ResponseEntity.ok(paciente);
    }

    // Cambiar estado del paciente (activo/inactivo)
    @PutMapping("/pacientes/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarMedico(session);
        pacienteService.interruptorEstado(id);
        return ResponseEntity.noContent().build();
    }


    // LOS MÉDICOS NO PUEDEN ELIMINAR PACIENTES
//    // Eliminar un paciente
//    @DeleteMapping("/pacientes/{id}")
//    public ResponseEntity<?> eliminarPaciente(@PathVariable Long id, HttpSession session) {
//        usuarioService.comprobarMedico(session);
//        pacienteService.eliminarPaciente(id);
//        return ResponseEntity.noContent().build();
//    }
}
