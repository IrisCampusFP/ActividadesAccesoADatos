package com.dam.accesodatos.centromedicora3_irisperez.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteUpdateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String nombre;
    private String apellidos;
    private String dni;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String historial;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
