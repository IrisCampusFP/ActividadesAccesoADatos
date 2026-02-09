package com.dam.accesodatos.recuperacionra3_irisperez.DTO;

import com.dam.accesodatos.recuperacionra3_irisperez.entity.Asignacion;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.DiaSemana;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaDTO {

    private Long id;
    private String nombre;
    private String zona;
    private DiaSemana dia_semana;
    private LocalTime hora_inicio;
    private LocalTime hora_fin;
    private Boolean activa;
    private Set<Asignacion> asignaciones;
}
