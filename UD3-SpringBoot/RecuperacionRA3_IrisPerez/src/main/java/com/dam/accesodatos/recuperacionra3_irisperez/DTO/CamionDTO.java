package com.dam.accesodatos.recuperacionra3_irisperez.DTO;

import com.dam.accesodatos.recuperacionra3_irisperez.entity.Asignacion;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.EstadoCamion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
    private Set<Asignacion> asignaciones;
}