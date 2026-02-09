package com.dam.accesodatos.recuperacionra3_irisperez.controller;

import com.dam.accesodatos.recuperacionra3_irisperez.DTO.UsuarioDTO;
import com.dam.accesodatos.recuperacionra3_irisperez.DTO.UsuarioUpdateDTO;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Camion;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Rol;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Ruta;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Usuario;
import com.dam.accesodatos.recuperacionra3_irisperez.service.CamionService;
import com.dam.accesodatos.recuperacionra3_irisperez.service.RolService;
import com.dam.accesodatos.recuperacionra3_irisperez.service.RutaService;
import com.dam.accesodatos.recuperacionra3_irisperez.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final CamionService camionService;
    private final RutaService rutaService;

    @Autowired
    public AdminController(UsuarioService usuarioService, RolService rolService, CamionService camionService, RutaService rutaService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.camionService = camionService;
        this.rutaService = rutaService;
    }


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
        UsuarioDTO usuario = usuarioService.obtenerUsuarioDTOPorId(id);
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

    // Obtener médicos disponibles
    @GetMapping("/usuarios/medico")
    public ResponseEntity<?> obtenerUsuariosMedico(HttpSession session) {
        usuarioService.comprobarAdmin(session);
        List<UsuarioDTO> medicos = usuarioService.obtenerUsuariosMedico();
        return ResponseEntity.ok(medicos);
    }

    // ···················
    //      CAMIONES
    // ···················
    
    // Crear un nuevo camion
    @PostMapping("/camiones")
    public ResponseEntity<?> crearCamion(@RequestBody Camion camion, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        try {
            Camion nuevoCamion = camionService.crearCamion(camion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCamion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("errorMsg", e.getMessage()));
        }
    }

    // Obtener todos los camiones
    @GetMapping("/camiones")
    public ResponseEntity<?> obtenerCamiones(HttpSession session) {
        usuarioService.comprobarAdmin(session);
        List<Camion> camiones = camionService.obtenerCamiones();
        return ResponseEntity.ok(camiones);
    }

    // Actualizar camion
    @PutMapping("/camiones/{id}")
    public ResponseEntity<?> actualizarCamion(@PathVariable Long id, @RequestBody Camion camionActualizado, HttpSession session) {
        usuarioService.comprobarAdmin(session);

        // Se actualiza el camion en la base de datos
        try {
            Camion camion = camionService.actualizarCamion(id, camionActualizado);
            return ResponseEntity.ok(camion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("errorMsg", e.getMessage()));
        }
    }

    // Obtener camion por ID
    @GetMapping("/camiones/{id}")
    public ResponseEntity<?> obtenerCamionPorId(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        Camion camion = camionService.obtenerCamionPorId(id);
        return ResponseEntity.ok(camion);
    }

    // Cambiar estado del camion (activo/inactivo)
    @PutMapping("/camiones/{id}/estado")
    public ResponseEntity<?> cambiarEstadoCamion(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        camionService.interruptorEstado(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminar un camion
    @DeleteMapping("/camiones/{id}")
    public ResponseEntity<?> eliminarCamion(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        camionService.eliminarCamion(id);
        return ResponseEntity.noContent().build();
    }

    
    // ···················
    //      RUTAS
    // ···················

    // Crear un nuevo ruta
    @PostMapping("/rutas")
    public ResponseEntity<?> crearRuta(@RequestBody Ruta ruta, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        try {
            Ruta nuevoRuta = rutaService.crearRuta(ruta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRuta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("errorMsg", e.getMessage()));
        }
    }

    // Obtener todos los rutas
    @GetMapping("/rutas")
    public ResponseEntity<?> obtenerRutas(HttpSession session) {
        usuarioService.comprobarAdmin(session);
        List<Ruta> rutas = rutaService.obtenerRutas();
        return ResponseEntity.ok(rutas);
    }

    // Actualizar ruta
    @PutMapping("/rutas/{id}")
    public ResponseEntity<?> actualizarRuta(@PathVariable Long id, @RequestBody Ruta rutaActualizado, HttpSession session) {
        usuarioService.comprobarAdmin(session);

        // Se actualiza el ruta en la base de datos
        try {
            Ruta ruta = rutaService.actualizarRuta(id, rutaActualizado);
            return ResponseEntity.ok(ruta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("errorMsg", e.getMessage()));
        }
    }

    // Obtener ruta por ID
    @GetMapping("/rutas/{id}")
    public ResponseEntity<?> obtenerRutaPorId(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        Ruta ruta = rutaService.obtenerRutaPorId(id);
        return ResponseEntity.ok(ruta);
    }

    // Cambiar estado de laruta (activo/inactivo)
    @PutMapping("/rutas/{id}/estado")
    public ResponseEntity<?> cambiarEstadoRuta(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        rutaService.interruptorEstado(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminar un ruta
    @DeleteMapping("/rutas/{id}")
    public ResponseEntity<?> eliminarRuta(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        rutaService.eliminarRuta(id);
        return ResponseEntity.noContent().build();

    }
}
