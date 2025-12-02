package com.dam.accesodatos.miprimeraapi.controller;

import com.dam.accesodatos.miprimeraapi.model.Tarea;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    @GetMapping
    public List<Tarea> obtenerTareas() {
        return Arrays.asList(
            new Tarea(1, "Estudiar Spring Boot", false),
            new Tarea(2, "Terminar práctica de Acceso a Datos", true),
            new Tarea(3, "Repasar Maven y dependencias", false)
        );
    }
}
