package com.dam.accesodatos.centromedicora3_irisperez.controller;

import com.dam.accesodatos.centromedicora3_irisperez.DTO.UsuarioDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vista")
public class AccesoVistaController {

    // Redirige a la vista correspondiente según el rol tras el login
    @GetMapping
    public String redirigirSegunRol(HttpSession session) {
        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuarioDTO");

        if (usuario != null) {
            if (usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("ADMIN"))) {
                return "ADMIN/menuAdminView";
            } else if (usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("MEDICO"))) {
                return "MEDICO/gestionPacientesMedico";
            } else if (usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("RECEPCION"))) {
                return "listaPacientes";
            } else {
                return "redirect:/killSession";
            }
        } else {
            return "redirect:/killSession";
        }
    }


    // Controla el acceso a la vista de gestión de usuarios para que solo accedan usuarios con rol ADMIN
    @GetMapping("/gestionUsuarios")
    public String accesoGestionUsuarios(HttpSession session) {
        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuarioDTO");

        if (usuario != null) {
            if (usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("ADMIN"))) {
                return "ADMIN/gestionUsuarios";
            } else {
                return "redirect:/killSession";
            }
        } else {
            return "redirect:/killSession";
        }
    }

    // Controla el acceso a la vista de gestión de pacientes para ADMIN
    @GetMapping("/gestionPacientesAdmin")
    public String accesoGestionPacientesAdmin(HttpSession session) {
        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuarioDTO");

        if (usuario != null) {
            if (usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("ADMIN")) ||
                    usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("MEDICO"))) {
                return "ADMIN/gestionPacientesAdmin";
            } else {
                return "redirect:/killSession";
            }
        } else {
            return "redirect:/killSession";
        }
    }

    // Controla el acceso a la vista de gestión de pacientes para MEDICO
    @GetMapping("/gestionPacientesMedico")
    public String accesoGestionPacientesMedico(HttpSession session) {
        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuarioDTO");

        if (usuario != null) {
            if (usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("ADMIN")) ||
                    usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("MEDICO"))) {
                return "MEDICO/gestionPacientesMedico";
            } else {
                return "redirect:/killSession";
            }
        } else {
            return "redirect:/killSession";
        }
    }

    // Controla el acceso a la vista de lista de pacientes para RECEPCION
    @GetMapping("/listaPacientes")
    public String accesoListaPacientes(HttpSession session) {
        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuarioDTO");

        if (usuario != null) {
            if (usuario.getRoles().stream().anyMatch(roles -> roles.getNombre().equalsIgnoreCase("RECEPCION"))) {
                return "RECEPCION/listaPacientes";
            } else {
                return "redirect:/killSession";
            }
        } else {
            return "redirect:/killSession";
        }
    }
}
