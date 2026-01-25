package com.dam.accesodatos.sistemacrudloginra3_irisperez.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * ENTIDAD: Usuario
 * Representa un usuario del sistema.
 * Mapea la tabla 'usuario' de la base de datos login_v2.
 *
 * Contiene:
 * - JPA (@Entity, @Table, @Id, @GeneratedValue, @Column)
 * - Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor)
 * - Callbacks del ciclo de vida (@PrePersist, @PreUpdate)
 * - Hash y verificación de contraseñas con BCrypt
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellidos", nullable = false, length = 150)
    private String apellidos;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "dni", nullable = false, unique = true, length = 9)
    private String dni;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "intentos_fallidos", nullable = false)
    private Integer intentosFallidos;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "rol_usuario",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles = new HashSet<>();


    // Constructor sin campos 'activo' y 'fechaCreacion' (se rellenarán con @PrePersist)
//    public Usuario(String nombre, String apellidos, String username, String email, String password, String dni) {
//        this.nombre = nombre;
//        this.apellidos = apellidos;
//        this.username = username;
//        this.email = email;
//        this.password = password;
//        this.dni = dni;
//    }

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
        if (fechaActualizacion == null) {
            fechaActualizacion = LocalDateTime.now();
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
        fechaActualizacion = LocalDateTime.now();
    }

    /*
     * Sobreescribo el setter 'setPassword()' para que
     * hashee la contraseña usando BCrypt.
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
