package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Proyecto {
    private int idProyecto;
    private String nombre;
    private BigDecimal presupuesto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Constructor vacío
    public Proyecto() {}

    // Constructor con parámetros
    public Proyecto(int idProyecto, String nombre, BigDecimal presupuesto, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idProyecto = idProyecto;
        this.nombre = nombre;
        this.presupuesto = presupuesto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters y Setters
    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(BigDecimal presupuesto) {
        this.presupuesto = presupuesto;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    @Override
    public String toString() {
        return "Proyecto{" +
                "idProyecto=" + idProyecto +
                ", nombre='" + nombre + '\'' +
                ", presupuesto=" + presupuesto +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                '}';
    }
}
