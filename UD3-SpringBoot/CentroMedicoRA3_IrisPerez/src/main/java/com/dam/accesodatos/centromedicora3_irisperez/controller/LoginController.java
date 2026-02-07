package com.dam.accesodatos.centromedicora3_irisperez.controller;

import com.dam.accesodatos.centromedicora3_irisperez.DTO.LoginRequestDTO;
import com.dam.accesodatos.centromedicora3_irisperez.DTO.UsuarioDTO;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Rol;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Usuario;
import com.dam.accesodatos.centromedicora3_irisperez.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class LoginController {
    @Autowired
    UsuarioService usuarioService;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> inicioSesion(@RequestBody LoginRequestDTO loginRequestDTO, HttpSession session) {
        try {
            if (usuarioService.comprobarPassword(loginRequestDTO.getPassword(), loginRequestDTO.getEmail())) {
                Usuario usuario = usuarioService.obtenerUsuarioPorEmail(loginRequestDTO.getEmail());

                Set<Rol> roles = new HashSet<>(usuario.getRoles());
                UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.getId(), usuario.getUsername(), usuario.getEmail(),
                        usuario.getNombre(), usuario.getActivo(), usuario.getFechaCreacion(), roles);
                session.setAttribute("usuarioDTO", usuarioDTO);
                return ResponseEntity.ok(usuarioDTO);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Contraseña incorrecta"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", e.getMessage())); // 403 Forbidden en caso de bloqueo
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/killSession")
    public String killSession(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
