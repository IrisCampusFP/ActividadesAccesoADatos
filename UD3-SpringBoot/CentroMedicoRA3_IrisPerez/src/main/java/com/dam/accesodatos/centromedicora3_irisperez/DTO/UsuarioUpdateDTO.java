package com.dam.accesodatos.centromedicora3_irisperez.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String username;
    private String email;
    private String nombre;
    private Boolean activo;
}
