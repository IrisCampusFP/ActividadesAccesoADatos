package exportadores;

import modelo.Movimiento;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import static utils.Utils.*;

public class ExportadorMovimientosJSON {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    private static final String INDENTACION = "    ";
    private static final String INDENTACION2 = INDENTACION + INDENTACION;
    private static final String INDENTACION3 = INDENTACION2 + INDENTACION;
    private static final String INDENTACION4 = INDENTACION2 + INDENTACION2;

    private static final String NODOPADRE = "listado_de_movimientos";
    private static final String NODOHIJO = "movimientos";

    public static boolean exportar(ArrayList<Movimiento> movimientos, String nombreArchivo) {
        // VALIDACIONES

        if (movimientos == null || movimientos.isEmpty()) {
            System.out.println("ERROR: No hay elementos para exportar.");
            return false;
        }

        String rutaCompleta =  CARPETA + File.separator + nombreArchivo + "_" + fecha + ".json";

        if (rutaCompleta == null || rutaCompleta.trim().isEmpty()) {
            System.out.println("ERROR: El nombre del archivo no puede estar vacío.");
            return false;
        }

        // CREAR DIRECTORIO
        if (crearDirectorio(CARPETA)) {
            // Utilizo OutputStreamWriter y FileOutputStream para aplicar UTF_8
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rutaCompleta), StandardCharsets.UTF_8))) {

                // 1. Apertura
                bw.write("{");
                bw.newLine();
                bw.write(INDENTACION + "\"" + NODOPADRE + "\": {");
                bw.newLine();


                // 2. Metadata
                fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")); // fecha y hora actual


                bw.write(INDENTACION2 + "\"metadata\": {");
                bw.newLine();
                bw.write(INDENTACION3 + "\"version\": \"1.0\",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"fecha\": \"" + escaparJSON(fecha) + "\",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"formato\": \"JSON\",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"totalMovimientos\": " + movimientos.size());
                bw.newLine();
                bw.write(INDENTACION2 + "},");
                bw.newLine();


                // 3. Lista de movimientos
                bw.write(INDENTACION2 + "\"" + NODOHIJO + "\": [");
                bw.newLine();
                for (int i = 0; i < movimientos.size(); i++) {
                    Movimiento m = movimientos.get(i);
                    bw.write(INDENTACION3 + "{");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"id\": \"" + escaparJSON(String.valueOf(i+1)) + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"tipo\": \"" + escaparJSON(m.getTipo()) + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"cantidad\": " + formatearDouble(m.getCantidad()) + ",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"fecha\": \"" + escaparJSON(m.getFecha()) + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"concepto\": \"" + escaparJSON(m.getConcepto()) + "\"");
                    bw.newLine();
                    bw.write(INDENTACION3 + "}" + (i < movimientos.size() - 1 ? "," : "")); // se encarga de cerrar el objeto JSON correspondiente a un movimiento y, dependiendo de si es el último movimiento de la lista o no, agrega una coma al final.
                    bw.newLine();
                }
                bw.write(INDENTACION2 + "],");
                bw.newLine();


                // 4. Resumen

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

                bw.write(INDENTACION2 + "\"resumen\": {");
                bw.newLine();
                bw.write(INDENTACION3 + "\"numMovimientos\": " + movimientos.size() + ",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"totalGastos\": " + formatearDouble(totalGastado) + ",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"totalIngresos\": " + formatearDouble(totalIngresado));
                bw.newLine();
                bw.write(INDENTACION2 + "}");
                bw.newLine();
                bw.write(INDENTACION + "}");
                bw.newLine();
                bw.write("}");

                return true;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false; // (no se ha creado el directorio)
    }
}