package nivel1Basico_listaDeEstudiantes;

import java.util.ArrayList;
import nivel1Basico_listaDeEstudiantes.exportadores.ExportadorEstudiantesCSV;
import nivel1Basico_listaDeEstudiantes.exportadores.ExportadorEstudiantesJSON;
import nivel1Basico_listaDeEstudiantes.exportadores.ExportadorEstudiantesXML;
import nivel1Basico_listaDeEstudiantes.modelo.Estudiante;

import static utils.Utils.*;

public class MainEstudiantes {
    private final static ArrayList<Estudiante> estudiantes = new ArrayList<>();

    public static void main(String[] args) {

        // Creo 5 estudiantes de ejemplo
        estudiantes.add(new Estudiante(1, "Lucía", "García López", 20, 8.5));
        estudiantes.add(new Estudiante(2, "Carlos", "Martínez Ruiz", 22, 7.2));
        estudiantes.add(new Estudiante(3, "Ana", "Sánchez Fernández", 19, 9.0));
        estudiantes.add(new Estudiante(4, "Miguel", "Pérez Gómez", 21, 6.8));
        estudiantes.add(new Estudiante(5, "Sofía", "Hernández Díaz", 20, 8.0));

        while (true) {
            System.out.println("\n······ MENÚ PRINCIPAL ·······");
            System.out.println("1. Exportar a CSV");
            System.out.println("2. Exportar a XML");
            System.out.println("3. Exportar a JSON");
            System.out.println("4. Exportar a TODOS los formatos");
            System.out.println("0. Salir");
            int opcion = leerOpcion();

            System.out.println();
            boolean exito = false;
            String nombreArchivo = "estudiantes";

            switch (opcion) {
                case 1:
                    exito = ExportadorEstudiantesCSV.exportar(estudiantes, nombreArchivo);
                    break;
                case 2: exito = ExportadorEstudiantesXML.exportar(estudiantes, nombreArchivo);
                    break;
                case 3: exito = ExportadorEstudiantesJSON.exportar(estudiantes, nombreArchivo);
                    break;
                case 4:
                    int exitosos = 0;
                    // Exportar CSV
                    System.out.println("1/3 Exportando a CSV...");
                    if (ExportadorEstudiantesCSV.exportar(estudiantes, nombreArchivo)) {
                        exitosos++;
                    }
                    // Exportar XML
                    System.out.println("2/3 Exportando a XML...");
                    if (ExportadorEstudiantesXML.exportar(estudiantes, nombreArchivo)) {
                        exitosos++;
                    }
                    // Exportar JSON
                    System.out.println("3/3 Exportando a JSON...");
                    if (ExportadorEstudiantesJSON.exportar(estudiantes, nombreArchivo)) {
                        exitosos++;
                    }
                    // Resumen
                    System.out.println("\nRESUMEN DE EXPORTACIÓN:");
                    System.out.println("Formatos exportados: " + exitosos + "/3\n");
                    if (exitosos == 3) {
                        exito = true;
                    } else {
                        System.out.println("⚠ No se han podido crear todos los archivos.");
                    }
                    break;
                case 0: System.out.println("Cerrando programa...");
                    sc.close();
                    return;
                default:
                    opcionInvalida();
                    continue;
            }
            if (exito) {
                System.out.println("¡EXPORTACIÓN COMPLETADA :)!");
                System.out.println("(Ubicación: exportaciones/)");
            } else {
                System.out.println("No se ha podido completar la exportación.");
            }
            pausar();
        }
    }
}
