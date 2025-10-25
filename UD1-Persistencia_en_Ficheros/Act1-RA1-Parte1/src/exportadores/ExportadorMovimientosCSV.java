package exportadores;

import modelo.Movimiento;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static utils.Utils.crearDirectorio;
import static utils.Utils.formatearDouble;

public class ExportadorMovimientosCSV {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    private static final String SEPARADOR = ";"; // Separador CSV


    // Escapar texto para CSV
    static String escaparCSV(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        // Si contiene el separador, comillas o saltos de línea, debemos escapar
        if (texto.contains(SEPARADOR) || texto.contains("\"") || texto.contains("\n")) {
            // Duplicamos las comillas y encerramos todo entre comillas
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }

        return texto;
    }

    public static boolean exportar(ArrayList<Movimiento> movimientos, String nombreArchivo) {

        // VALIDACIONES

        if (movimientos == null || movimientos.isEmpty()) {
            System.out.println("ERROR: No hay elementos para exportar.");
            return false;
        }

        String rutaCompleta =  CARPETA + File.separator + nombreArchivo + "_" + fecha + ".csv";

        if (rutaCompleta == null || rutaCompleta.trim().isEmpty()) {
            System.out.println("ERROR: El nombre del archivo no puede estar vacío.");
            return false;
        }

        // CREAR DIRECTORIO

        if (crearDirectorio(CARPETA)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaCompleta))) {

                // 1. ESCRIBIR ENCABEZADO
                escribirEncabezado(writer);

                // 2. ESCRIBIR CADA MOVIMIENTO
                for (Movimiento m : movimientos) {
                    escribirMovimiento(writer, m);
                }

                // 3. ESCRIBIR RESUMEN
                escribirResumen(writer, movimientos);


                return true;

            } catch (IOException e) {
                System.out.println("Error al escribir el archivo CSV: " + e.getMessage());
            }
        }
        return false; // (no se ha creado el directorio)
    }

    // Encabezado (nombres de las columnas)
    private static void escribirEncabezado(BufferedWriter writer) throws IOException {
        writer.write("Tipo" + SEPARADOR);
        writer.write("Cantidad" + SEPARADOR);
        writer.write("Fecha" + SEPARADOR);
        writer.write("Concepto"); // El último elemento SIN SEPARADOR
        writer.newLine(); // Salto de línea al final
    }

    // Escribe todos los campos de cada movimiento
    private static void escribirMovimiento(BufferedWriter writer, Movimiento m) throws IOException {
        // Cada campo separado por el delimitador
        writer.write(escaparCSV(m.getTipo()) + SEPARADOR);
        writer.write(formatearDouble(m.getCantidad()) + SEPARADOR);
        writer.write(escaparCSV(m.getFecha()) + SEPARADOR);
        writer.write(escaparCSV(m.getConcepto())); // El último elemento SIN SEPARADOR
        writer.newLine(); // Salto de línea al final
    }

    // Escribe el Resumen
    private static void escribirResumen(BufferedWriter writer, ArrayList<Movimiento> movimientos) throws IOException {
        // Cálculos
        double totalGastado = 0;
        double totalIngresado = 0;
        for (Movimiento mov : movimientos) {
            if (mov.getTipo().equalsIgnoreCase("ingreso")) {
                totalIngresado += mov.getCantidad();
            } else {
                totalGastado -= mov.getCantidad();
            }
        }

        writer.newLine(); // Línea en blanco (Para separar el resumen de los elementos de arriba)

        writer.write("# RESUMEN");
        writer.newLine();
        writer.write("# Total movimientos" + SEPARADOR + movimientos.size());
        writer.newLine();
        writer.write("# Total gastos" + SEPARADOR + formatearDouble(totalGastado));
        writer.newLine();
        writer.write("# Total ingresos" + SEPARADOR + formatearDouble(totalIngresado));
        writer.newLine();
    }
}
