package com.dam.accesodatos.centromedicora3_irisperez.repository;

import com.dam.accesodatos.centromedicora3_irisperez.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Obtener pacientes activos (activo = true)
    List<Paciente> findByActivoTrue();

    // Comprobar si existe un usuario con dni
    boolean existsByDni(String dni);

}
