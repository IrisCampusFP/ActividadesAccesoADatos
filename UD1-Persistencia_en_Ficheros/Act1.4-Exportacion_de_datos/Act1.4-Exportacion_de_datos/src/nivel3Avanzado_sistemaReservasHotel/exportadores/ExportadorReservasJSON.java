package nivel3Avanzado_sistemaReservasHotel.exportadores;

import nivel3Avanzado_sistemaReservasHotel.modelo.Reserva;
import nivel3Avanzado_sistemaReservasHotel.modelo.Cliente;
import nivel3Avanzado_sistemaReservasHotel.modelo.Habitacion;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static utils.Utils.*;

public class ExportadorReservasJSON {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    private static final String INDENTACION = "    ";
    private static final String INDENTACION2 = INDENTACION + INDENTACION;
    private static final String INDENTACION3 = INDENTACION2 + INDENTACION;
    private static final String INDENTACION4 = INDENTACION3 + INDENTACION;

    public static boolean exportar(ArrayList<Reserva> reservas, String nombreArchivo) {
        // VALIDACIONES

        if (reservas == null || reservas.isEmpty()) {
            System.out.println("ERROR: No hay elementos para exportar.");
            return false;
        }

        String rutaCompleta = CARPETA + File.separator + nombreArchivo + "_" + fecha + ".json";

        if (rutaCompleta == null || rutaCompleta.trim().isEmpty()) {
            System.out.println("ERROR: El nombre del archivo no puede estar vacío.");
            return false;
        }

        // CREAR DIRECTORIO
        if (crearDirectorio(CARPETA)) {
            // Utilizo OutputStreamWriter y FileOutputStream para aplicar UTF_8
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rutaCompleta), StandardCharsets.UTF_8))) {

                // 1. APERTURA DEL OBJETO RAÍZ
                bw.write("{");
                bw.newLine();
                bw.write(INDENTACION + "\"hotel\": {");
                bw.newLine();

                // 2. INFORMACIÓN GENERAL
                String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                bw.write(INDENTACION2 + "\"informacion\": {");
                bw.newLine();
                bw.write(INDENTACION3 + "\"nombre\": \"Hotel Paradise Iris Pérez\",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"fecha\": \"" + escaparJSON(fechaActual) + "\"");
                bw.newLine();
                bw.write(INDENTACION2 + "},");
                bw.newLine();

                // 3. GENERAR MAPAS ÚNICOS DE CLIENTES Y HABITACIONES
                HashMap<Integer, Cliente> clientesUnicos = new HashMap<>();
                HashMap<Integer, Habitacion> habitacionesUnicas = new HashMap<>();
                for (Reserva r : reservas) {
                    clientesUnicos.put(r.getCliente().getId(), r.getCliente());
                    habitacionesUnicas.put(r.getHabitacion().getNumero(), r.getHabitacion());
                }

                // 4. CLIENTES
                bw.write(INDENTACION2 + "\"clientes\": {");
                bw.newLine();
                int contClientes = 0;
                for (Integer id : clientesUnicos.keySet()) {
                    Cliente c = clientesUnicos.get(id);
                    bw.write(INDENTACION3 + "\"" + id + "\": {");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"nombre\": \"" + escaparJSON(c.getNombre()) + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"email\": \"" + escaparJSON(c.getEmail()) + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"telefono\": \"" + escaparJSON(c.getTelefono()) + "\"");
                    bw.newLine();
                    bw.write(INDENTACION3 + "}" + (++contClientes < clientesUnicos.size() ? "," : ""));
                    bw.newLine();
                }
                bw.write(INDENTACION2 + "},");
                bw.newLine();

                // 5. HABITACIONES
                bw.write(INDENTACION2 + "\"habitaciones\": {");
                bw.newLine();
                int contHabitaciones = 0;
                for (Integer num : habitacionesUnicas.keySet()) {
                    Habitacion h = habitacionesUnicas.get(num);
                    bw.write(INDENTACION3 + "\"" + num + "\": {");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"tipo\": \"" + escaparJSON(h.getTipo()) + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"precioPorNoche\": " + formatearDouble(h.getPrecioPorNoche()) + ",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"disponible\": " + h.getDisponible());
                    bw.newLine();
                    bw.write(INDENTACION3 + "}" + (++contHabitaciones < habitacionesUnicas.size() ? "," : ""));
                    bw.newLine();
                }
                bw.write(INDENTACION2 + "},");
                bw.newLine();

                // 6. RESERVAS
                bw.write(INDENTACION2 + "\"reservas\": [");
                bw.newLine();
                DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                for (int i = 0; i < reservas.size(); i++) {
                    Reserva r = reservas.get(i);
                    bw.write(INDENTACION3 + "{");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"id\": " + r.getId() + ",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"clienteId\": " + r.getCliente().getId() + ",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"habitacionNumero\": " + r.getHabitacion().getNumero() + ",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"fechaEntrada\": \"" + r.getFechaEntrada().format(formatoFecha) + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"fechaSalida\": \"" + r.getFechaSalida().format(formatoFecha) + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"noches\": " + r.getNoches() + ",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"precioTotal\": " + formatearDouble(r.getPrecioTotal()) + ",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"estado\": \"" + escaparJSON(r.getEstado()) + "\"");
                    bw.newLine();
                    bw.write(INDENTACION3 + "}" + (i < reservas.size() - 1 ? "," : ""));
                    bw.newLine();
                }
                bw.write(INDENTACION2 + "],");
                bw.newLine();

                // 7. ESTADÍSTICAS
                // --- CÁLCULOS ---
                HashMap<String, Integer> reservasPorTipo = new HashMap<>();
                HashMap<String, Double> ingresosPorTipo = new HashMap<>();
                HashMap<String, Integer> reservasPorEstado = new HashMap<>();
                int nochesReservadas = 0;
                double ingresosTotal = 0;

                for (Reserva r : reservas) {
                    String tipo = r.getHabitacion().getTipo();
                    String estado = r.getEstado();
                    reservasPorTipo.put(tipo, reservasPorTipo.getOrDefault(tipo, 0) + 1);
                    ingresosPorTipo.put(tipo, ingresosPorTipo.getOrDefault(tipo, 0.0) + r.getPrecioTotal());
                    reservasPorEstado.put(estado, reservasPorEstado.getOrDefault(estado, 0) + 1);
                    nochesReservadas += r.getNoches();
                    ingresosTotal += r.getPrecioTotal();
                }

                // Porcentaje por tipo de habitación
                HashMap<String, Double> porcentajePorTipo = new HashMap<>();
                for (String tipo : reservasPorTipo.keySet()) {
                    double porcentaje = reservas.size() > 0 ? (reservasPorTipo.get(tipo) * 100.0) / reservas.size() : 0;
                    porcentajePorTipo.put(tipo, porcentaje);
                }
                // Ocupación media
                double ocupacionMedia = reservas.size() > 0 ? (nochesReservadas * 100.0) / reservas.size() : 0;

                bw.write(INDENTACION2 + "\"estadisticas\": {");
                bw.newLine();

                // Por tipo de habitación
                bw.write(INDENTACION3 + "\"porTipoHabitacion\": {");
                bw.newLine();
                int tiposEscritos = 0;
                for (String tipo : reservasPorTipo.keySet()) {
                    bw.write(INDENTACION4 + "\"" + escaparJSON(tipo) + "\": {");
                    bw.newLine();
                    bw.write(INDENTACION4 + INDENTACION + "\"totalReservas\": " + reservasPorTipo.get(tipo) + ",");
                    bw.newLine();
                    bw.write(INDENTACION4 + INDENTACION + "\"ingresos\": " + formatearDouble(ingresosPorTipo.get(tipo)) + ",");
                    bw.newLine();
                    bw.write(INDENTACION4 + INDENTACION + "\"porcentaje\": " + formatearDouble(porcentajePorTipo.get(tipo)));
                    bw.newLine();
                    bw.write(INDENTACION4 + "}" + (++tiposEscritos < reservasPorTipo.size() ? "," : ""));
                    bw.newLine();
                }
                bw.write(INDENTACION3 + "},");
                bw.newLine();

                // Por estado
                bw.write(INDENTACION3 + "\"porEstado\": {");
                bw.newLine();
                int estadosEscritos = 0;
                for (String estado : reservasPorEstado.keySet()) {
                    bw.write(INDENTACION4 + "\"" + escaparJSON(estado) + "\": " + reservasPorEstado.get(estado) + ( ++estadosEscritos < reservasPorEstado.size() ? "," : ""));
                    bw.newLine();
                }
                bw.write(INDENTACION3 + "},");
                bw.newLine();

                // Resumen global
                bw.write(INDENTACION3 + "\"resumen\": {");
                bw.newLine();
                bw.write(INDENTACION4 + "\"totalReservas\": " + reservas.size() + ",");
                bw.newLine();
                bw.write(INDENTACION4 + "\"ingresosTotal\": " + formatearDouble(ingresosTotal) + ",");
                bw.newLine();
                bw.write(INDENTACION4 + "\"nochesReservadas\": " + nochesReservadas + ",");
                bw.newLine();
                bw.write(INDENTACION4 + "\"ocupacionMedia\": " + formatearDouble(ocupacionMedia));
                bw.newLine();
                bw.write(INDENTACION3 + "}");
                bw.newLine();

                bw.write(INDENTACION2 + "}"); // cierre estadisticas
                bw.newLine();

                bw.write(INDENTACION + "}"); // cierre hotel
                bw.newLine();
                bw.write("}"); // cierre raíz

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