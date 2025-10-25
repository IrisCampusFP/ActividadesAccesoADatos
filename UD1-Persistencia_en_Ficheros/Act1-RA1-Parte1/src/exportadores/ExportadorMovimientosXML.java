package exportadores;

import modelo.Movimiento;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import static utils.Utils.crearDirectorio;
import static utils.Utils.formatearDouble;

public class ExportadorMovimientosXML {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    private static final String INDENTACION = "    ";
    private static final String INDENTACION2 = INDENTACION + INDENTACION;
    private static final String INDENTACION3 = INDENTACION2 + INDENTACION;

    private static final String NODOPADRE = "listado_de_movimientos";
    private static final String NODOHIJO = "movimientos";

    static String escaparXML(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }

        // IMPORTANTE: El orden importa - escapar & primero
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    public static boolean exportar(ArrayList<Movimiento> movimientos, String nombreArchivo) {
        try {

            // VALIDACIONES

            if (movimientos == null || movimientos.isEmpty()) {
                System.out.println("ERROR: No hay elementos para exportar.");
                return false;
            }

            String rutaCompleta =  CARPETA + File.separator + nombreArchivo + "_" + fecha + ".xml";

            if (rutaCompleta == null || rutaCompleta.trim().isEmpty()) {
                System.out.println("ERROR: El nombre del archivo no puede estar vacío.");
                return false;
            }

            // CREAR DIRECTORIO

            if (crearDirectorio(CARPETA)) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaCompleta))) {

                    // 1. Declaración XML y elemento raíz
                    bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    bw.newLine();
                    bw.write("<" + NODOPADRE + ">");
                    bw.newLine();


                    // 2. Metadata
                    fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")); // fecha y hora actual

                    bw.write(INDENTACION + "<metadata>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<version>1.0</version>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<fecha>" + escaparXML(fecha) + "</fecha>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<totalMovimientos>" + movimientos.size() + "</totalMovimientos>");
                    bw.newLine();
                    bw.write(INDENTACION + "</metadata>");
                    bw.newLine();


                    // 3. Lista de movimientos
                    bw.write(INDENTACION + "<" + NODOHIJO + ">");
                    bw.newLine();
                    int id = 1;
                    for (Movimiento e : movimientos) {
                        bw.write(INDENTACION2 + "<movimiento id=\"" + id++ + "\">");
                        bw.newLine();
                        bw.write(INDENTACION3 + "<tipo>" + escaparXML(e.getTipo()) + "</tipo>");
                        bw.newLine();
                        bw.write(INDENTACION3 + "<cantidad>" + formatearDouble(e.getCantidad()) + "</cantidad>");
                        bw.newLine();
                        bw.write(INDENTACION3 + "<fecha>" + escaparXML(e.getFecha()) + "</fecha>");
                        bw.newLine();
                        bw.write(INDENTACION3 + "<concepto>" + escaparXML(e.getConcepto()) + "</concepto>");
                        bw.newLine();
                        bw.write(INDENTACION2 + "</movimiento>");
                        bw.newLine();
                    }
                    bw.write(INDENTACION + "</" + NODOHIJO + ">");
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

                    bw.write(INDENTACION + "<resumen>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<numMovimientos>" + movimientos.size() + "</numMovimientos>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<totalGastos>" + formatearDouble(totalGastado) + "</totalGastos>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<totalIngresos>" + formatearDouble(totalIngresado) + "</totalIngresos>");
                    bw.newLine();
                    bw.write(INDENTACION + "</resumen>");
                    bw.newLine();

                    bw.write("</" + NODOPADRE + ">"); // Cierro el nodo padre
                    bw.newLine();
                    return true;
                }
            }
            return false; // (no se ha creado el directorio)
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo XML: " + e.getMessage());
            return false;
        }
    }
}
