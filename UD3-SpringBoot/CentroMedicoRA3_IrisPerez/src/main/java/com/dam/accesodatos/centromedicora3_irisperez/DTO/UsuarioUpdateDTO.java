package com.dam.accesodatos.centromedicora3_irisperez.DTO;

import com.dam.accesodatos.centromedicora3_irisperez.entity.Paciente;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
