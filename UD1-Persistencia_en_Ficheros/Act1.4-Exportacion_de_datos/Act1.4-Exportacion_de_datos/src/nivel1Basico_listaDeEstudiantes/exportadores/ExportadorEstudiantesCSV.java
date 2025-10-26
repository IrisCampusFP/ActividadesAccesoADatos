package nivel1Basico_listaDeEstudiantes.exportadores;

import nivel1Basico_listaDeEstudiantes.modelo.Estudiante;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static utils.Utils.*;

public class ExportadorEstudiantesCSV {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    private static final String SEPARADOR = ";"; // Separador CSV

    public static boolean exportar(ArrayList<Estudiante> estudiantes, String nombreArchivo) {

        // VALIDACIONES

        if (estudiantes == null || estudiantes.isEmpty()) {
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
                for (Estudiante e : estudiantes) {
                    escribirEstudiante(writer, e);
                }

                // 3. ESCRIBIR RESUMEN
                escribirResumen(writer, estudiantes);


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
        writer.write("Nombre" + SEPARADOR);
        writer.write("Apellidos" + SEPARADOR);
        writer.write("Edad" + SEPARADOR);
        writer.write("Nota"); // El último elemento SIN SEPARADOR
        writer.newLine(); // Salto de línea al final
    }

    // Escribe todos los campos de cada estudiante
    private static void escribirEstudiante(BufferedWriter writer, Estudiante e) throws IOException {
        // Cada campo separado por el delimitador
        writer.write(e.getId() + SEPARADOR);
        writer.write(escaparCSV(e.getNombre()) + SEPARADOR);
        writer.write(escaparCSV(e.getApellidos()) + SEPARADOR);
        writer.write(e.getEdad() + SEPARADOR);
        writer.write(formatearDouble(e.getNota())); // El último elemento SIN SEPARADOR
        writer.newLine(); // Salto de línea al final
    }

    // Escribe el Resumen
    private static void escribirResumen(BufferedWriter writer, ArrayList<Estudiante> estudiantes) throws IOException {
        // Cálculos
        double sumaNotas = 0;
        for (Estudiante e : estudiantes) {
             sumaNotas += e.getNota();
        }

        double notaMedia = sumaNotas / estudiantes.size(); // Suma de todas las notas entre el número de estudiantes

        writer.newLine(); // Línea en blanco (Para separar el resumen de los elementos de arriba)

        writer.write("# Nota media" + SEPARADOR + formatearDouble(notaMedia));
        writer.newLine();
    }
}
