package nivel1Basico_listaDeEstudiantes.exportadores;

import nivel1Basico_listaDeEstudiantes.modelo.Estudiante;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import static utils.Utils.*;

public class ExportadorEstudiantesJSON {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    private static final String INDENTACION = "    ";
    private static final String INDENTACION2 = INDENTACION + INDENTACION;
    private static final String INDENTACION3 = INDENTACION2 + INDENTACION;
    private static final String INDENTACION4 = INDENTACION2 + INDENTACION2;

    private static final String NODOPADRE = "clase";
    private static final String NODOHIJO = "estudiantes";

    public static boolean exportar(ArrayList<Estudiante> estudiantes, String nombreArchivo) {
        // VALIDACIONES

        if (estudiantes == null || estudiantes.isEmpty()) {
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
                bw.write(INDENTACION3 + "\"totalEstudiantes\": " + estudiantes.size());
                bw.newLine();
                bw.write(INDENTACION2 + "},");
                bw.newLine();


                // 3. Lista de estudiantes
                bw.write(INDENTACION2 + "\"" + NODOHIJO + "\": [");
                bw.newLine();
                for (int i = 0; i < estudiantes.size(); i++) {
                    Estudiante e = estudiantes.get(i);
                    bw.write(INDENTACION3 + "{");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"id\": \"" + e.getId() + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"nombre\": \"" + escaparJSON(e.getNombre()) + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"apellidos\": \"" + escaparJSON(e.getApellidos()) + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"edad\": \"" + e.getEdad() + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"nota\": \"" + formatearDouble(e.getNota()) + "\"");
                    bw.newLine();
                    bw.write(INDENTACION3 + "}" + (i < estudiantes.size() - 1 ? "," : "")); // se encarga de cerrar el objeto JSON correspondiente a un estudiante y, dependiendo de si es el último estudiante de la lista o no, agrega una coma al final.
                    bw.newLine();
                }
                bw.write(INDENTACION2 + "],");
                bw.newLine();


                // 4. Estadísticas

                // Cálculos
                double sumaNotas = 0;
                double notaMaxima = estudiantes.getFirst().getNota();
                double notaMinima = estudiantes.getFirst().getNota();
                int aprobados = 0;
                int suspensos = 0;
                for (Estudiante e : estudiantes) {
                    double nota = e.getNota();
                    sumaNotas += nota;
                    if (nota > notaMaxima) notaMaxima = nota;
                    if (nota < notaMinima) notaMinima = nota;
                    if (nota >= 5) aprobados++; else suspensos++;
                }
                double notaMedia = sumaNotas / estudiantes.size();

                bw.write(INDENTACION2 + "\"estadisticas\": {");
                bw.newLine();
                bw.write(INDENTACION3 + "\"notaMedia\": " + formatearDouble(notaMedia) + ",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"notaMaxima\": " + formatearDouble(notaMaxima) + ",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"notaMinima\": " + formatearDouble(notaMinima) + ",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"aprobados\": " + aprobados + ",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"suspensos\": " + suspensos); // Ultimo elemento sin ','
                bw.newLine();
                bw.write(INDENTACION2 + "}");
                bw.newLine();

                bw.write(INDENTACION + "}"); // Cierre del nodo hijo
                bw.newLine();
                bw.write("}"); // Cierre del nodo padre

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