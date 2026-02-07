package com.dam.accesodatos.centromedicora3_irisperez.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Atributo que no existe en la BD, solo para controlar la logica
    // de intentos fallidos en el inicio de sesión
    @Transient
    private Integer intentosFallidos;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "rol_usuario",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles = new HashSet<>();

    @OneToMany(mappedBy = "medico", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paciente> pacientes = new ArrayList<>();


    /*
     * Callback JPA que se ejecuta antes de insertar la entidad.
     * Inicializa valores por defecto.
     */
    @PrePersist
    public void prePersist() {
        if (activo == null) {
            activo = true;
        }
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (intentosFallidos == null) {
            intentosFallidos = 0;
        }
    }

    /*
     * Callback JPA que se ejecuta antes de actualizar la entidad.
     */
    @PreUpdate
    public void preUpdate() {
    }

    /*
     * Setter para 'password' que hashea la contraseña
     * antes de guardarla usando BCrypt.
     */
    public void setPasswordHash(String passwordUsuario) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(passwordUsuario);
    }

    /*
     * Comprueba si una contraseña
     * coincide con el hash almacenado.
     */
    public boolean checkPassword(String passwordUsuario) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(passwordUsuario, this.password);
    }

}
