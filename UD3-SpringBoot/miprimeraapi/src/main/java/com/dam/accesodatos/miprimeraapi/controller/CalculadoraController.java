package com.dam.accesodatos.miprimeraapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calculadora")
public class CalculadoraController {

    @GetMapping("/sumar/{num1}/{num2}")
    public double sumar(@PathVariable double num1, @PathVariable double num2) {
        return num1 + num2;
    }

    @GetMapping("/restar/{num1}/{num2}")
    public double restar(@PathVariable double num1, @PathVariable double num2) {
        return num1 - num2;
    }

    @GetMapping("/multiplicar/{num1}/{num2}")
    public double multiplicar(@PathVariable double num1, @PathVariable double num2) {
        return num1 * num2;
    }
}
