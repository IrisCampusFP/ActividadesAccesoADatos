package com.dam.accesodatos.centromedicora3_irisperez.controller;

import com.dam.accesodatos.centromedicora3_irisperez.entity.Rol;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Usuario;
import com.dam.accesodatos.centromedicora3_irisperez.service.RolService;
import com.dam.accesodatos.centromedicora3_irisperez.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    RolService rolService;

    // Crear un nuevo usuario (metodo POST)
    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        try {
            Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("msg", e.getMessage()));
        }
    }

    // Obtener todos los usuarios (metodo GET)
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> obtenerUsuarios(HttpSession session) {
        usuarioService.comprobarAdmin(session);
        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // Actualizar usuario (metodo PUT)
    // (Se utiliza PathVariable para asegurarnos de actualizar el usuario correspondiente)
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Map<String, Object> datosActualizados, HttpSession session) {
        usuarioService.comprobarAdmin(session);

        // Se obtienen los datos originales del usuario
        Optional<Usuario> usuarioOptional = usuarioService.obtenerUsuarioPorId(id);
        Usuario usuario = usuarioOptional.get();

        // Se modifican los campos actualizables con los datos recibidos
        usuario.setNombre((String) datosActualizados.get("nombre"));
        usuario.setUsername((String) datosActualizados.get("username"));
        usuario.setEmail((String) datosActualizados.get("email"));
        usuario.setActivo((Boolean) datosActualizados.get("activo"));

        // Se actualiza el usuario en la base de datos
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("msg", e.getMessage()));
        }
    }

    // Obtener usuario por ID (metodo GET)
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return ResponseEntity.ok(usuario);
    }

    // Cambiar estado del usuario (activo/inactivo) (metodo PUT)
    @PutMapping("/usuarios/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        usuarioService.interruptorEstado(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminar un usuario (metodo DELETE)
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // Cambiar contraseña (metodo PUT)
    @PutMapping("/usuarios/{id}/password")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String passwordActual = body.get("passwordActual");
        String passwordNueva = body.get("passwordNueva");

        try {
            usuarioService.cambiarPassword(id, passwordActual, passwordNueva);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // Contraseña actual incorrecta o contraseña nueva vacía
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (IllegalStateException e) {
            // Usuario inactivo
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error al cambiar la contraseña"));
        }
    }


    // Obtener todos los roles (metodo GET)
    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> obtenerRoles(HttpSession session) {
        usuarioService.comprobarAdmin(session);
        List<Rol> roles = rolService.obtenerRoles();
        return ResponseEntity.ok(roles);
    }

    // Asignar roles (metodo PUT)
    @PutMapping("/usuarios/{id}/roles")
    public ResponseEntity<Void> actualizarRoles(@PathVariable Long id, @RequestBody List<Long> idsRoles, HttpSession session) {
        usuarioService.comprobarAdmin(session);
        usuarioService.actualizarRolesUsuario(id, idsRoles);
        return ResponseEntity.noContent().build();
    }

}
