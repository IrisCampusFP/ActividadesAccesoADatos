package model;

import java.math.BigDecimal;

public class Empleado {
    private int idEmpleado;
    private String nombre;
    private String departamento;
    private BigDecimal salario;
    private boolean activo;

    // Constructor vacío
    public Empleado() {}

    // Constructor con parámetros
    public Empleado(int idEmpleado, String nombre, String departamento, BigDecimal salario, boolean activo) {
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.departamento = departamento;
        this.salario = salario;
        this.activo = activo;
    }

    // Getters y Setters
    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "idEmpleado=" + idEmpleado +
                ", nombre='" + nombre + '\'' +
                ", departamento='" + departamento + '\'' +
                ", salario=" + salario +
                ", activo=" + activo +
                '}';
    }
}
