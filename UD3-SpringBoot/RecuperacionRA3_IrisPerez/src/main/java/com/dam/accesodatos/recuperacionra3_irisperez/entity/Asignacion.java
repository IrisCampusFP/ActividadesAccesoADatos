//package com.dam.accesodatos.recuperacionra3_irisperez.entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.io.Serial;
//import java.io.Serializable;
//import java.time.LocalDateTime;
//import java.util.HashSet;
//import java.util.Set;
//
//@Table(name = "asignaciones")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class Asignacion implements Serializable {
//    @Serial
//    private static final long serialVersionUID = 1L;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Long id;
//
//    @Column(name = "camion_id", nullable = false)
//    private String camion_id;
//
//    @Column(name = "ruta_id", nullable = false)
//    private String ruta_id;
//
//    @Column(name = "fecha_asignacion", nullable = false)
//    private LocalDateTime fecha_asignacion;
//
//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "camion_id")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private Camion camion;
//
//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ruta_id")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private Ruta ruta;
//
//    /*
//     * Callback JPA que se ejecuta antes de insertar la entidad.
//     * Inicializa valores por defecto.
//     */
//    @PrePersist
//    public void prePersist() {
//        if (fecha_asignacion == null) {
//            fecha_asignacion = LocalDateTime.now();
//        }
//    }
//
//    /*
//     * Callback JPA que se ejecuta antes de actualizar la entidad.
//     */
//    @PreUpdate
//    public void preUpdate() {
//    }
//}
