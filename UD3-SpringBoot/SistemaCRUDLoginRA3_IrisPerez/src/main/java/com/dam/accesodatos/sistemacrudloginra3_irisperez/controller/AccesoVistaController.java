package com.dam.accesodatos.sistemacrudloginra3_irisperez.controller;

import com.dam.accesodatos.sistemacrudloginra3_irisperez.DTO.UsuarioDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vista")
public class AccesoVistaController {
    @GetMapping
    public String redirigirSegunRol(HttpSession session) {
        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuarioDTO");

        if (usuario != null) {
            if (usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("admin"))) {
                return "admin/vistaAdmin";
            } else if (usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("user"))) {
                return "user/vistaUser";
            } else {
                return "redirect:/killSession";
            }
        } else {
            return "redirect:/killSession";
        }
    }
}
