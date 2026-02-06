package com.dam.accesodatos.centromedicora3_irisperez.controller;

import com.dam.accesodatos.centromedicora3_irisperez.DTO.PacienteDTO;
import com.dam.accesodatos.centromedicora3_irisperez.DTO.UsuarioDTO;
import com.dam.accesodatos.centromedicora3_irisperez.DTO.UsuarioUpdateDTO;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Paciente;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Rol;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Usuario;
import com.dam.accesodatos.centromedicora3_irisperez.service.PacienteService;
import com.dam.accesodatos.centromedicora3_irisperez.service.RolService;
import com.dam.accesodatos.centromedicora3_irisperez.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    RolService rolService;

    @Autowired
    PacienteService pacienteService;

    // ···················
    //      USUARIOS
    // ···················

    // Crear un nuevo usuario
    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        try {
            UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("errorMsg", e.getMessage()));
        }
    }

    // Obtener todos los usuarios
    @GetMapping("/usuarios")
    public ResponseEntity<?> obtenerUsuarios(HttpSession session) {
        usuarioService.comprobarAdmin(session);
        List<UsuarioDTO> usuarios = usuarioService.obtenerUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // Actualizar usuario
    // (Se utiliza PathVariable para asegurarnos de actualizar el usuario correspondiente)
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioUpdateDTO usuarioActualizado, HttpSession session) {
        usuarioService.comprobarAdmin(session);

        // Se actualiza el usuario en la base de datos
        try {
            UsuarioDTO usuario = usuarioService.actualizarUsuario(id, usuarioActualizado);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("errorMsg", e.getMessage()));
        }
    }

    // Obtener usuario por ID
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        UsuarioDTO usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }

    // Cambiar estado del usuario (activo/inactivo)
    @PutMapping("/usuarios/{id}/estado")
    public ResponseEntity<?> cambiarEstadoUsuario(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        usuarioService.interruptorEstado(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminar un usuario
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // Cambiar contraseña
    @PutMapping("/usuarios/{id}/password")
    public ResponseEntity<?> cambiarPasswordUsuario(
            @PathVariable Long id,
            @RequestBody Map<String, String> body, HttpSession session) {

        usuarioService.comprobarAdmin(session);

        String passwordActual = body.get("passwordActual");
        String passwordNueva = body.get("passwordNueva");

        try {
            usuarioService.cambiarPassword(id, passwordActual, passwordNueva);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // Contraseña actual incorrecta o contraseña nueva vacía
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("errorMsg", e.getMessage()));
        } catch (IllegalStateException e) {
            // Usuario inactivo
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("errorMsg", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errorMsg", "Error al cambiar la contraseña"));
        }
    }

    // Obtener todos los roles
    @GetMapping("/usuarios/roles")
    public ResponseEntity<?>obtenerRolesUsuario(HttpSession session) {
        usuarioService.comprobarAdmin(session);
        List<Rol> roles = rolService.obtenerRoles();
        return ResponseEntity.ok(roles);
    }

    // Asignar roles
    @PutMapping("/usuarios/roles/{id}")
    public ResponseEntity<?> actualizarRolesUsuario(@PathVariable Long id, @RequestBody List<Long> idsRoles, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        usuarioService.actualizarRolesUsuario(id, idsRoles);
        return ResponseEntity.noContent().build();
    }


    // ···················
    //      PACIENTES
    // ···················

    // Crear un nuevo paciente
    @PostMapping("/pacientes")
    public ResponseEntity<?> crearPaciente(@RequestBody Paciente paciente, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        try {
            PacienteDTO nuevoPaciente = pacienteService.crearPaciente(paciente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPaciente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("errorMsg", e.getMessage()));
        }
    }

    // Obtener todos los pacientes
    @GetMapping("/pacientes")
    public ResponseEntity<?> obtenerPacientes(HttpSession session) {
        usuarioService.comprobarAdmin(session);
        List<PacienteDTO> pacientes = pacienteService.obtenerPacientes();
        return ResponseEntity.ok(pacientes);
    }

    // Actualizar paciente
    // (Se utiliza PathVariable para asegurarnos de actualizar el paciente correspondiente)
    @PutMapping("/pacientes/{id}")
    public ResponseEntity<?> actualizarPaciente(@PathVariable Long id, @RequestBody PacienteDTO pacienteActualizado, HttpSession session) {
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
    public ResponseEntity<?> cambiarEstadoPaciente(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarMedico(session);
        pacienteService.interruptorEstado(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminar un paciente
    @DeleteMapping("/pacientes/{id}")
    public ResponseEntity<?> eliminarPaciente(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarMedico(session);
        pacienteService.eliminarPaciente(id);
        return ResponseEntity.noContent().build();
    }
}
