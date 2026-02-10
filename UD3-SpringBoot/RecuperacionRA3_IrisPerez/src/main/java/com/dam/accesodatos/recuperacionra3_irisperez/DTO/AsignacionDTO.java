package com.dam.accesodatos.recuperacionra3_irisperez.DTO;

import com.dam.accesodatos.recuperacionra3_irisperez.entity.DiaSemana;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionDTO {

    private Long id;
    private LocalDate fechaAsignacion;

    // Datos del camión
    private Long camionId;
    private String matricula;
    private String modelo;

    // Datos de la ruta
    private Long rutaId;
    private String nombreRuta;
    private String zona;
    private DiaSemana diaSemana;
}
