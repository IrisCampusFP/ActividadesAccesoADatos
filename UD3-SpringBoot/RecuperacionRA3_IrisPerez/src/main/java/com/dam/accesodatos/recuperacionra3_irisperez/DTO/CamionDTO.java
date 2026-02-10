package com.dam.accesodatos.recuperacionra3_irisperez.DTO;

import com.dam.accesodatos.recuperacionra3_irisperez.entity.EstadoCamion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CamionDTO {
    private Long id;
    private String matricula;
    private String modelo;
    private BigDecimal capacidad_kg;
    private EstadoCamion estado;
    private LocalDate fechaAlta;
    private Boolean activo;
    private Integer asignaciones; // Número de asignaciones
}