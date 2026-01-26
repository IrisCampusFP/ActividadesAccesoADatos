package com.dam.accesodatos.centromedicora3_irisperez.controller;

import com.dam.accesodatos.centromedicora3_irisperez.DTO.UsuarioDTO;
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
            if (usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("ADMIN"))) {
                return "admin/vistaAdmin";
            } else if (usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("MEDICO"))) {
                return "medico/vistaMedico";
            } else if (usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("RECEPCION"))) {
                return "recepcion/vistaRecepcion";
            } else {
                return "redirect:/killSession";
            }
        } else {
            return "redirect:/killSession";
        }
    }
}
