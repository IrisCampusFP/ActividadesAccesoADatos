package nivel3Avanzado_sistemaReservasHotel.exportadores;

import nivel3Avanzado_sistemaReservasHotel.modelo.Reserva;
import nivel3Avanzado_sistemaReservasHotel.modelo.Cliente;
import nivel3Avanzado_sistemaReservasHotel.modelo.Habitacion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static utils.Utils.*;

public class ExportadorReservasXML {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    private static final String INDENTACION = "    ";
    private static final String INDENTACION2 = INDENTACION + INDENTACION;
    private static final String INDENTACION3 = INDENTACION2 + INDENTACION;
    private static final String INDENTACION4 = INDENTACION3 + INDENTACION;

    public static boolean exportar(ArrayList<Reserva> reservas, String nombreArchivo) {
        try {

            // VALIDACIONES

            if (reservas == null || reservas.isEmpty()) {
                System.out.println("ERROR: No hay elementos para exportar.");
                return false;
            }

            String rutaCompleta = CARPETA + File.separator + nombreArchivo + "_" + fecha + ".xml";

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
                    bw.write("<hotel>");
                    bw.newLine();

                    // 2. Información general
                    String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    bw.write(INDENTACION + "<informacion>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<nombre>Hotel Paradise Iris Pérez</nombre>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<fecha>" + escaparXML(fechaActual) + "</fecha>");
                    bw.newLine();
                    bw.write(INDENTACION + "</informacion>");
                    bw.newLine();

                    // 3. Reservas con información anidada
                    bw.write(INDENTACION + "<reservas totalReservas=\"" + reservas.size() + "\">");
                    bw.newLine();

                    DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    // Para estadísticas
                    HashMap<String, Integer> reservasPorTipo = new HashMap<>();
                    HashMap<String, Double> ingresosPorTipo = new HashMap<>();
                    HashMap<String, Integer> reservasPorEstado = new HashMap<>();
                    int nochesReservadas = 0;
                    double ingresosTotal = 0;

                    for (Reserva r : reservas) {
                        // Actualizar estadísticas
                        String tipo = r.getHabitacion().getTipo();
                        String estado = r.getEstado();
                        reservasPorTipo.put(tipo, reservasPorTipo.getOrDefault(tipo, 0) + 1);
                        ingresosPorTipo.put(tipo, ingresosPorTipo.getOrDefault(tipo, 0.0) + r.getPrecioTotal());
                        reservasPorEstado.put(estado, reservasPorEstado.getOrDefault(estado, 0) + 1);
                        nochesReservadas += r.getNoches();
                        ingresosTotal += r.getPrecioTotal();

                        // Inicio reserva
                        bw.write(INDENTACION2 + "<reserva id=\"" + r.getId() + "\" estado=\"" + escaparXML(estado) + "\">");
                        bw.newLine();

                        // Cliente
                        Cliente c = r.getCliente();
                        bw.write(INDENTACION3 + "<cliente>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<id>" + c.getId() + "</id>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<nombre>" + escaparXML(c.getNombre()) + "</nombre>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<email>" + escaparXML(c.getEmail()) + "</email>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<telefono>" + escaparXML(c.getTelefono()) + "</telefono>");
                        bw.newLine();
                        bw.write(INDENTACION3 + "</cliente>");
                        bw.newLine();

                        // Habitacion
                        Habitacion h = r.getHabitacion();
                        bw.write(INDENTACION3 + "<habitacion numero=\"" + h.getNumero() + "\" tipo=\"" + escaparXML(h.getTipo()) + "\">");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<precioPorNoche>" + formatearDouble(h.getPrecioPorNoche()) + "</precioPorNoche>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<disponible>" + h.getDisponible() + "</disponible>");
                        bw.newLine();
                        bw.write(INDENTACION3 + "</habitacion>");
                        bw.newLine();

                        // Fechas
                        bw.write(INDENTACION3 + "<fechas>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<entrada>" + r.getFechaEntrada().format(formatoFecha) + "</entrada>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<salida>" + r.getFechaSalida().format(formatoFecha) + "</salida>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<noches>" + r.getNoches() + "</noches>");
                        bw.newLine();
                        bw.write(INDENTACION3 + "</fechas>");
                        bw.newLine();

                        // Precio
                        bw.write(INDENTACION3 + "<precio>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<total>" + formatearDouble(r.getPrecioTotal()) + "</total>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<porNoche>" + formatearDouble(h.getPrecioPorNoche()) + "</porNoche>");
                        bw.newLine();
                        bw.write(INDENTACION3 + "</precio>");
                        bw.newLine();

                        // Fin reserva
                        bw.write(INDENTACION2 + "</reserva>");
                        bw.newLine();
                    }
                    bw.write(INDENTACION + "</reservas>");
                    bw.newLine();

                    // 4. Estadísticas
                    bw.write(INDENTACION + "<estadisticas>");
                    bw.newLine();

                    // Por tipo de habitación
                    bw.write(INDENTACION2 + "<porTipoHabitacion>");
                    bw.newLine();
                    for (String tipo : reservasPorTipo.keySet()) {
                        bw.write(INDENTACION3 + "<" + escaparXML(tipo) + ">");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<totalReservas>" + reservasPorTipo.get(tipo) + "</totalReservas>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<ingresos>" + formatearDouble(ingresosPorTipo.get(tipo)) + "</ingresos>");
                        bw.newLine();
                        bw.write(INDENTACION3 + "</" + escaparXML(tipo) + ">");
                        bw.newLine();
                    }
                    bw.write(INDENTACION2 + "</porTipoHabitacion>");
                    bw.newLine();

                    // Por estado
                    bw.write(INDENTACION2 + "<porEstado>");
                    bw.newLine();
                    for (String estado : reservasPorEstado.keySet()) {
                        bw.write(INDENTACION3 + "<" + escaparXML(estado) + ">" + reservasPorEstado.get(estado) + "</" + escaparXML(estado) + ">");
                        bw.newLine();
                    }
                    bw.write(INDENTACION2 + "</porEstado>");
                    bw.newLine();

                    // Resumen global
                    bw.write(INDENTACION2 + "<resumen>");
                    bw.newLine();
                    bw.write(INDENTACION3 + "<totalReservas>" + reservas.size() + "</totalReservas>");
                    bw.newLine();
                    bw.write(INDENTACION3 + "<ingresosTotal>" + formatearDouble(ingresosTotal) + "</ingresosTotal>");
                    bw.newLine();
                    bw.write(INDENTACION3 + "<nochesReservadas>" + nochesReservadas + "</nochesReservadas>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "</resumen>");
                    bw.newLine();

                    bw.write(INDENTACION + "</estadisticas>");
                    bw.newLine();

                    bw.write("</hotel>");
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