package modelo;

import java.io.Serializable;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String nombre;
    private final String DNI;
    private final String numeroCuenta;

    public Cliente(String nombre, String DNI, String numeroCuenta) {
        this.nombre = nombre;
        this.DNI = DNI;
        this.numeroCuenta = numeroCuenta;
    }

    public String getNombre() { return nombre; }
    public String getDni() { return DNI; }
    public String getNumeroCuenta() { return numeroCuenta; }

    @Override
    public String toString() { return nombre + " | " + DNI + " | " + numeroCuenta; }
}