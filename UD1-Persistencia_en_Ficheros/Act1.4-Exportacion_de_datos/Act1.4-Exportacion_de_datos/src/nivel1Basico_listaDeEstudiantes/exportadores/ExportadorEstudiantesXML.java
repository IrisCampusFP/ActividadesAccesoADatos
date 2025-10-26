package nivel1Basico_listaDeEstudiantes.exportadores;

import nivel1Basico_listaDeEstudiantes.modelo.Estudiante;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import static utils.Utils.*;

public class ExportadorEstudiantesXML {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    private static final String INDENTACION = "    ";
    private static final String INDENTACION2 = INDENTACION + INDENTACION;
    private static final String INDENTACION3 = INDENTACION2 + INDENTACION;

    private static final String NODOPADRE = "clase";
    private static final String NODOHIJO = "estudiantes";

    public static boolean exportar(ArrayList<Estudiante> estudiantes, String nombreArchivo) {
        try {

            // VALIDACIONES

            if (estudiantes == null || estudiantes.isEmpty()) {
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
                    bw.write(INDENTACION2 + "<totalEstudiantes>" + estudiantes.size() + "</totalEstudiantes>");
                    bw.newLine();
                    bw.write(INDENTACION + "</metadata>");
                    bw.newLine();


                    // 3. Lista de estudiantes
                    bw.write(INDENTACION + "<" + NODOHIJO + ">");
                    bw.newLine();
                    for (Estudiante e : estudiantes) {
                        bw.write(INDENTACION2 + "<estudiante id=\"" + e.getId() + "\">");
                        bw.newLine();
                        bw.write(INDENTACION3 + "<nombre>" + escaparXML(e.getNombre()) + "</nombre>");
                        bw.newLine();
                        bw.write(INDENTACION3 + "<apellidos>" + escaparXML(e.getApellidos()) + "</apellidos>");
                        bw.newLine();
                        bw.write(INDENTACION3 + "<edad>" + e.getEdad() + "</edad>");
                        bw.newLine();
                        bw.write(INDENTACION3 + "<nota>" + formatearDouble(e.getNota()) + "</nota>");
                        bw.newLine();
                        bw.write(INDENTACION2 + "</estudiante>");
                        bw.newLine();
                    }
                    bw.write(INDENTACION + "</" + NODOHIJO + ">");
                    bw.newLine();


                    // 4. Resumen

                    // Cálculos
                    double sumaNotas = 0;
                    double notaMaxima = estudiantes.getFirst().getNota();
                    double notaMinima = estudiantes.getFirst().getNota();
                    for (Estudiante e : estudiantes) {
                        double nota = e.getNota();
                        sumaNotas += nota;
                        if (nota > notaMaxima) notaMaxima = nota;
                        if (nota < notaMinima) notaMinima = nota;
                    }

                    double notaMedia = sumaNotas / estudiantes.size();

                    bw.write(INDENTACION + "<resumen>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<notaMedia>" + formatearDouble(notaMedia) + "</notaMedia>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<notaMaxima>" + formatearDouble(notaMaxima) + "</notaMaxima>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<notaMinima>" + formatearDouble(notaMinima) + "</notaMinima>");
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
