package com.dam.accesodatos.sistemacrudloginra3_irisperez.DTO;

import com.dam.accesodatos.sistemacrudloginra3_irisperez.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String nombre;
    private String apellidos;
    private String username;
    private String email;
    private String dni;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime ultimoLogin;
    private Set<Rol> roles;
}
