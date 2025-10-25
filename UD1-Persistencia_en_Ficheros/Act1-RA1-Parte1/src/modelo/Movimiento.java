package modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Movimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String tipo; // (Ingreso o Retirada)
    private final double cantidad;
    private final String fecha;
    private final String concepto;

    // Constructor para crear nuevos movimientos
    public Movimiento(String tipo, double cantidad, String concepto) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        this.concepto = concepto;
    }

    public String getTipo() {
        return tipo;
    }

    public double getCantidad() {
        return cantidad;
    }

    public String getFecha() {
        return fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    @Override
    public String toString() {
         return tipo + " | " + cantidad + " | " + fecha + " | " + concepto;
    }
}