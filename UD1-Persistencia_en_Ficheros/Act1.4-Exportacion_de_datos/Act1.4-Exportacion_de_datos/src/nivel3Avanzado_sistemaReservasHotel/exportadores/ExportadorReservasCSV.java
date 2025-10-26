package nivel3Avanzado_sistemaReservasHotel.exportadores;

import nivel3Avanzado_sistemaReservasHotel.modelo.Reserva;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import static utils.Utils.*;

public class ExportadorReservasCSV {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    private static final String SEPARADOR = ";"; // Separador CSV

    public static boolean exportar(ArrayList<Reserva> reservas, String nombreArchivo) {

        // VALIDACIONES

        if (reservas == null || reservas.isEmpty()) {
            System.out.println("ERROR: No hay elementos para exportar.");
            return false;
        }

        String rutaCompleta = CARPETA + File.separator + nombreArchivo + "_" + fecha + ".csv";

        if (rutaCompleta == null || rutaCompleta.trim().isEmpty()) {
            System.out.println("ERROR: El nombre del archivo no puede estar vacío.");
            return false;
        }

        // CREAR DIRECTORIO

        if (crearDirectorio(CARPETA)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaCompleta))) {

                // 1. ESCRIBIR ENCABEZADO
                escribirEncabezado(writer);

                // 2. ESCRIBIR CADA RESERVA
                for (Reserva r : reservas) {
                    escribirReserva(writer, r);
                }

                // 3. ESCRIBIR RESUMEN
                escribirResumen(writer, reservas);

                return true;

            } catch (IOException e) {
                System.out.println("Error al escribir el archivo CSV: " + e.getMessage());
            }
        }
        return false; // (no se ha creado el directorio)
    }

    // Encabezado (nombres de las columnas)
    private static void escribirEncabezado(BufferedWriter writer) throws IOException {
        writer.write("ID" + SEPARADOR);
        writer.write("ClienteNombre" + SEPARADOR);
        writer.write("ClienteEmail" + SEPARADOR);
        writer.write("ClienteTelefono" + SEPARADOR);
        writer.write("HabitacionNum" + SEPARADOR);
        writer.write("TipoHabitacion" + SEPARADOR);
        writer.write("PrecioNoche" + SEPARADOR);
        writer.write("FechaEntrada" + SEPARADOR);
        writer.write("FechaSalida" + SEPARADOR);
        writer.write("Noches" + SEPARADOR);
        writer.write("PrecioTotal" + SEPARADOR);
        writer.write("Estado"); // El último elemento SIN SEPARADOR
        writer.newLine();
    }

    // Escribe todos los campos de cada reserva (aplanando relaciones)
    private static void escribirReserva(BufferedWriter writer, Reserva r) throws IOException {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        writer.write(r.getId() + SEPARADOR);
        writer.write(escaparCSV(r.getCliente().getNombre()) + SEPARADOR);
        writer.write(escaparCSV(r.getCliente().getEmail()) + SEPARADOR);
        writer.write(escaparCSV(r.getCliente().getTelefono()) + SEPARADOR);
        writer.write(r.getHabitacion().getNumero() + SEPARADOR);
        writer.write(escaparCSV(r.getHabitacion().getTipo()) + SEPARADOR);
        writer.write(formatearDouble(r.getHabitacion().getPrecioPorNoche()) + SEPARADOR);
        writer.write(r.getFechaEntrada().format(formatoFecha) + SEPARADOR);
        writer.write(r.getFechaSalida().format(formatoFecha) + SEPARADOR);
        writer.write(r.getNoches() + SEPARADOR);
        writer.write(formatearDouble(r.getPrecioTotal()) + SEPARADOR);
        writer.write(escaparCSV(r.getEstado()));
        writer.newLine();
    }

    // Escribe el resumen
    private static void escribirResumen(BufferedWriter writer, ArrayList<Reserva> reservas) throws IOException {
        // CÁLCULOS
        int totalReservas = reservas.size();
        double sumaTotal = 0;
        int confirmadas = 0, canceladas = 0, completadas = 0;

        for (Reserva r : reservas) {
            sumaTotal += r.getPrecioTotal();
            switch (r.getEstado().toLowerCase()) {
                case "confirmada": confirmadas++; break;
                case "cancelada": canceladas++; break;
                case "completada": completadas++; break;
            }
        }

        writer.newLine();
        writer.write("# Total reservas: " + totalReservas);
        writer.newLine();
        writer.write("# Total ingresos (reservas): " + formatearDouble(sumaTotal));
        writer.newLine();
        writer.write("# Confirmadas: " + confirmadas + " | Canceladas: " + canceladas + " | Completadas: " + completadas);
        writer.newLine();
    }
}