package nivel2Intermedio_bibliotecaLibros;

import nivel2Intermedio_bibliotecaLibros.exportadores.ExportadorLibrosCSV;
import nivel2Intermedio_bibliotecaLibros.exportadores.ExportadorLibrosXML;
import nivel2Intermedio_bibliotecaLibros.exportadores.ExportadorLibrosJSON;
import nivel2Intermedio_bibliotecaLibros.modelo.Libro;

import java.util.ArrayList;

import static utils.Utils.*;

public class MainBiblioteca {
    private final static ArrayList<Libro> libros = new ArrayList<>();

    public static void main(String[] args) {

        // Creación de libros de ejemplo
        libros.add(new Libro("978-84-123", "El Quijote", "Miguel de Cervantes", "Ficción", 1605, 863, true, 150));
        libros.add(new Libro("978-84-456", "Cien años de soledad", "Gabriel García Márquez", "Ficción", 1967, 471, false, 98));
        libros.add(new Libro("978-84-789", "Breve historia del tiempo", "Stephen Hawking", "Ciencia", 1988, 256, true, 73));
        libros.add(new Libro("978-84-101", "Introducción a la programación", "Ana Torres", "Ciencia", 2010, 350, true, 45));
        libros.add(new Libro("978-84-202", "Historia universal", "VV.AA.", "Historia", 2005, 590, false, 60));
        libros.add(new Libro("978-84-123", "El Quijote", "Miguel de Cervantes", "Ficción", 1605, 863, true, 150));
        libros.add(new Libro("978-84-456", "Cien años de soledad", "Gabriel García Márquez", "Ficción", 1967, 471, false, 98));
        libros.add(new Libro("978-84-789", "Breve historia del tiempo", "Stephen Hawking", "Ciencia", 1988, 256, true, 73));
        libros.add(new Libro("978-84-101", "Introducción a la programación", "Ana Torres", "Ciencia", 2010, 350, true, 45));
        libros.add(new Libro("978-84-202", "Historia universal", "VV.AA.", "Historia", 2005, 590, false, 60));
        libros.add(new Libro("978-84-303", "Los pilares de la Tierra", "Ken Follett", "Ficción", 1989, 1076, true, 110));
        libros.add(new Libro("978-84-404", "Sapiens: De animales a dioses", "Yuval Noah Harari", "Historia", 2011, 496, true, 85));
        libros.add(new Libro("978-84-505", "La teoría del todo", "Stephen Hawking", "Ciencia", 2002, 224, false, 40));
        libros.add(new Libro("978-84-606", "Fundación", "Isaac Asimov", "Ficción", 1951, 320, true, 135));
        libros.add(new Libro("978-84-707", "Un mundo feliz", "Aldous Huxley", "Ficción", 1932, 288, false, 90));
        libros.add(new Libro("978-84-808", "El origen de las especies", "Charles Darwin", "Ciencia", 1859, 502, true, 51));
        libros.add(new Libro("978-84-909", "La Segunda Guerra Mundial", "Antony Beevor", "Historia", 2012, 1152, false, 58));
        libros.add(new Libro("978-84-010", "El nombre de la rosa", "Umberto Eco", "Ficción", 1980, 672, true, 120));
        libros.add(new Libro("978-84-111", "Crónica de una muerte anunciada", "Gabriel García Márquez", "Ficción", 1981, 128, true, 80));
        libros.add(new Libro("978-84-212", "Cosmos", "Carl Sagan", "Ciencia", 1980, 384, false, 62));
        libros.add(new Libro("978-84-313", "Guns, Germs, and Steel", "Jared Diamond", "Historia", 1997, 528, true, 70));

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
            String nombreArchivo = "libros";

            switch (opcion) {
                case 1:
                    exito = ExportadorLibrosCSV.exportar(libros, nombreArchivo);
                    break;
                case 2:
                    exito = ExportadorLibrosXML.exportar(libros, nombreArchivo);
                    break;
                case 3:
                    exito = ExportadorLibrosJSON.exportar(libros, nombreArchivo);
                    break;
                case 4:
                    int exitosos = 0;
                    // Exportar CSV
                    System.out.println("1/3 Exportando a CSV...");
                    if (ExportadorLibrosCSV.exportar(libros, nombreArchivo)) {
                        exitosos++;
                    }
                    // Exportar XML
                    System.out.println("2/3 Exportando a XML...");
                    if (ExportadorLibrosXML.exportar(libros, nombreArchivo)) {
                        exitosos++;
                    }
                    // Exportar JSON
                    System.out.println("3/3 Exportando a JSON...");
                    if (ExportadorLibrosJSON.exportar(libros, nombreArchivo)) {
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
                case 0:
                    System.out.println("Cerrando programa...");
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