package com.dam.accesodatos.recuperacionra3_irisperez.DTO;

import com.dam.accesodatos.recuperacionra3_irisperez.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private String email;
    private String nombre;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private Set<Rol> roles;
}
