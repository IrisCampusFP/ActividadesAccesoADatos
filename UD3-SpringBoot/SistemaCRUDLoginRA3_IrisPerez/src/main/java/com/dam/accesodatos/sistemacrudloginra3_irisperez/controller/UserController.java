package com.dam.accesodatos.sistemacrudloginra3_irisperez.controller;

import com.dam.accesodatos.sistemacrudloginra3_irisperez.DTO.UsuarioDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @ResponseBody
    @GetMapping("/datos")
    public ResponseEntity<UsuarioDTO> obtenerDatosUsuario(HttpSession session) {

        UsuarioDTO usuarioDTO = (UsuarioDTO) session.getAttribute("usuarioDTO");

        if (usuarioDTO != null) {
            try{
                // Se devuelven los datos del usuario como JSON
                return ResponseEntity.ok(usuarioDTO);
            } catch (Exception e){
                System.out.println("Error al obtener los datos del usuario");
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
