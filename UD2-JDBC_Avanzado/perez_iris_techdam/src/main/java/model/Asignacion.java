package model;

import java.time.LocalDate;

public class Asignacion {
    private int idAsignacion;
    private int idEmpleado;
    private int idProyecto;
    private LocalDate fecha;

    // Constructor vacío
    public Asignacion() {}

    // Constructor con parámetros
    public Asignacion(int idAsignacion, int idEmpleado, int idProyecto, LocalDate fecha) {
        this.idAsignacion = idAsignacion;
        this.idEmpleado = idEmpleado;
        this.idProyecto = idProyecto;
        this.fecha = fecha;
    }

    // Getters y Setters
    public int getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Asignacion{" +
                "idAsignacion=" + idAsignacion +
                ", idEmpleado=" + idEmpleado +
                ", idProyecto=" + idProyecto +
                ", fecha=" + fecha +
                '}';
    }
}
