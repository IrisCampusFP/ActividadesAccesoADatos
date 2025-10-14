import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Movimiento {
    private String tipo; // (ingreso o retirada)
    private double cantidad;
    private String fecha;
    private String concepto;

    // Constructor para crear nuevos movimientos
    public Movimiento(String tipo, double cantidad, String concepto) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        this.concepto = concepto;
    }

    // Constructor para cargar movimientos anteriores
    public Movimiento(String tipo, double cantidad, String fecha, String concepto) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.concepto = concepto;
    }

    public String getTipo() { return tipo; }
    public double getCantidad() { return cantidad; }
    public String getFecha() { return fecha; }
    public String getConcepto() { return concepto; }

    // Metodo para obtener cada movimiento del archivo, pasándolo de texto a objeto obteniendo sus campos
    public static Movimiento parse(String linea) {
        String[] partes = linea.split(";"); // Separo cada campo del movimiento con split(";")
        if (partes.length != 4) return null; // Si no tiene exactamente 4 campos no lo considero un movimiento válido y devuelvo null
        try {
            double cantidad = Double.parseDouble(partes[1].replace(",", ".")); // Uso Double.parseDouble para convertir el saldo de texto a double
            return new Movimiento(partes[0], cantidad, partes[2], partes[3]); // Devuelvo el objeto movimiento con todos sus campos
        } catch (Exception e) {
            return null;
        }
    }

    public void guardarMovimiento(String rutaMovimientos) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaMovimientos, true))) { // Escribo en el archivo movimientos en modo append (true)
            bw.write(String.format("%s;%.2f;%s;%s", tipo, cantidad, fecha, concepto));
            bw.newLine(); // Salto de línea
        }
    }

    @Override
    // Metodo para mostrar todos los campos de un movimiento en forma de texto por consola
    public String toString() {
        return String.format("%s | %.2f € | %s | %s", tipo, cantidad, fecha, concepto);
    }
}