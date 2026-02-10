package com.dam.accesodatos.recuperacionra3_irisperez.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "rutas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "asignaciones")
public class Ruta implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "zona", nullable = false, length = 100)
    private String zona;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana dia_semana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime hora_inicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime hora_fin;

    @Column(name = "activa", nullable = false)
    private Boolean activa;

    // Reemplazar el @ManyToMany por:
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Asignacion> asignaciones = new HashSet<>();

    /*
     * Callback JPA que se ejecuta antes de insertar la entidad.
     * Inicializa valores por defecto.
     */
    @PrePersist
    public void prePersist() {
        if (activa == null) {
            activa = true;
        }
    }

    /*
     * Callback JPA que se ejecuta antes de actualizar la entidad.
     */
    @PreUpdate
    public void preUpdate() {
    }
}
