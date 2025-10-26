
import nivel1Basico_listaDeEstudiantes.exportadores.ExportadorEstudiantesCSV;
import nivel1Basico_listaDeEstudiantes.exportadores.ExportadorEstudiantesXML;
import nivel1Basico_listaDeEstudiantes.exportadores.ExportadorEstudiantesJSON;
import nivel1Basico_listaDeEstudiantes.modelo.Estudiante;

import nivel2Intermedio_bibliotecaLibros.exportadores.ExportadorLibrosCSV;
import nivel2Intermedio_bibliotecaLibros.exportadores.ExportadorLibrosXML;
import nivel2Intermedio_bibliotecaLibros.exportadores.ExportadorLibrosJSON;
import nivel2Intermedio_bibliotecaLibros.modelo.Libro;

import nivel3Avanzado_sistemaReservasHotel.exportadores.ExportadorReservasCSV;
import nivel3Avanzado_sistemaReservasHotel.exportadores.ExportadorReservasXML;
import nivel3Avanzado_sistemaReservasHotel.exportadores.ExportadorReservasJSON;
import nivel3Avanzado_sistemaReservasHotel.modelo.Cliente;
import nivel3Avanzado_sistemaReservasHotel.modelo.Habitacion;
import nivel3Avanzado_sistemaReservasHotel.modelo.Reserva;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import static utils.Utils.*;

public class Main {
    // Nivel 1: Estudiantes
    private final static ArrayList<Estudiante> estudiantes = new ArrayList<>();

    // Nivel 2: Libros
    private final static ArrayList<Libro> libros = new ArrayList<>();

    // Nivel 3: Reservas hotel
    private final static ArrayList<Reserva> reservas = new ArrayList<>();

    public static void main(String[] args) {

        // Agrego 5 estudiantes de ejemplo
        estudiantes.add(new Estudiante(1, "Lucía", "García López", 20, 8.5));
        estudiantes.add(new Estudiante(2, "Carlos", "Martínez Ruiz", 22, 7.2));
        estudiantes.add(new Estudiante(3, "Ana", "Sánchez Fernández", 19, 9.0));
        estudiantes.add(new Estudiante(4, "Miguel", "Pérez Gómez", 21, 6.8));
        estudiantes.add(new Estudiante(5, "Sofía", "Hernández Díaz", 20, 8.0));


        // Agrego libros de ejemplo
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


        // Creo clientes de ejemplo
        Cliente cliente1 = new Cliente(1, "Juan García", "juan@email.com", "666111222");
        Cliente cliente2 = new Cliente(2, "María López", "maria@email.com", "666222333");
        Cliente cliente3 = new Cliente(3, "Pedro Sánchez", "pedro@email.com", "666333444");

        // Creo habitaciones de ejemplo
        Habitacion hab1 = new Habitacion(101, "Doble", 90.00, false);
        Habitacion hab2 = new Habitacion(205, "Suite", 200.00, true);
        Habitacion hab3 = new Habitacion(303, "Individual", 50.00, true);
        Habitacion hab4 = new Habitacion(404, "Doble", 80.00, true);
        Habitacion hab5 = new Habitacion(505, "Suite", 200.00, false);

        // Agrego reservas de ejemplo
        reservas.add(new Reserva(1, cliente1, hab1, LocalDate.parse("2025-10-20"), LocalDate.parse("2025-10-23"), 3, 270.00, "Confirmada"));
        reservas.add(new Reserva(2, cliente2, hab2, LocalDate.parse("2025-10-21"), LocalDate.parse("2025-10-25"), 4, 800.00, "Confirmada"));
        reservas.add(new Reserva(3, cliente3, hab3, LocalDate.parse("2025-10-18"), LocalDate.parse("2025-10-20"), 2, 100.00, "Completada"));
        reservas.add(new Reserva(4, cliente1, hab4, LocalDate.parse("2025-10-22"), LocalDate.parse("2025-10-24"), 2, 160.00, "Cancelada"));
        reservas.add(new Reserva(5, cliente2, hab3, LocalDate.parse("2025-10-25"), LocalDate.parse("2025-10-28"), 3, 150.00, "Confirmada"));
        reservas.add(new Reserva(6, cliente3, hab5, LocalDate.parse("2025-10-26"), LocalDate.parse("2025-10-29"), 3, 600.00, "Confirmada"));
        reservas.add(new Reserva(7, cliente1, hab1, LocalDate.parse("2025-10-28"), LocalDate.parse("2025-10-31"), 3, 270.00, "Completada"));
        reservas.add(new Reserva(8, cliente2, hab4, LocalDate.parse("2025-10-27"), LocalDate.parse("2025-10-30"), 3, 240.00, "Confirmada"));
        reservas.add(new Reserva(9, cliente3, hab2, LocalDate.parse("2025-10-29"), LocalDate.parse("2025-11-01"), 3, 600.00, "Confirmada"));
        reservas.add(new Reserva(10, cliente1, hab3, LocalDate.parse("2025-10-30"), LocalDate.parse("2025-11-01"), 2, 100.00, "Confirmada"));

        while (true) {
            System.out.println("\n······ MENÚ PRINCIPAL ·······");
            System.out.println("1. Exportar estudiantes");
            System.out.println("2. Exportar libros");
            System.out.println("3. Exportar reservas de hotel");
            System.out.println("0. Salir");
            int opcion = leerOpcion();

            System.out.println();
            switch (opcion) {
                case 1:
                    menuExportarEstudiantes();
                    break;
                case 2:
                    menuExportarLibros();
                    break;
                case 3:
                    menuExportarReservasHotel();
                    break;
                case 0:
                    System.out.println("Cerrando programa...");
                    sc.close();
                    return;
                default:
                    opcionInvalida();
                    continue;
            }
            pausar();
        }
    }

    private static int mostrarMenuElegirFormato() {
        System.out.println("··· ELIGE UN FORMATO ···");
        System.out.println("1. Exportar a CSV");
        System.out.println("2. Exportar a XML");
        System.out.println("3. Exportar a JSON");
        System.out.println("4. Exportar a TODOS los formatos");
        System.out.println("0. Volver al menú principal");
        return leerOpcion();
    }

    private static void menuExportarEstudiantes() {
        int opcion = mostrarMenuElegirFormato();
        String nombreArchivo = "estudiantes";
        boolean exito;
        System.out.println();
        switch (opcion) {
            case 1:
                exito = ExportadorEstudiantesCSV.exportar(estudiantes, nombreArchivo);
                break;
            case 2:
                exito = ExportadorEstudiantesXML.exportar(estudiantes, nombreArchivo);
                break;
            case 3:
                exito = ExportadorEstudiantesJSON.exportar(estudiantes, nombreArchivo);
                break;
            case 4:
                int exitosos = 0;
                System.out.println("1/3 Exportando a CSV...");
                if (ExportadorEstudiantesCSV.exportar(estudiantes, nombreArchivo)) exitosos++;
                System.out.println("2/3 Exportando a XML...");
                if (ExportadorEstudiantesXML.exportar(estudiantes, nombreArchivo)) exitosos++;
                System.out.println("3/3 Exportando a JSON...");
                if (ExportadorEstudiantesJSON.exportar(estudiantes, nombreArchivo)) exitosos++;
                System.out.println("\nRESUMEN DE EXPORTACIÓN:");
                System.out.println("Formatos exportados: " + exitosos + "/3\n");
                exito = true;
                if (exitosos != 3) System.out.println("⚠ No se han podido crear todos los archivos.");
                break;
            case 0:
                System.out.println("Volviendo al menú principal...");
                return;
            default:
                opcionInvalida();
                return;
        }
        mostrarMensajeExito(exito);
    }

    private static void menuExportarLibros() {
        int opcion = mostrarMenuElegirFormato();
        String nombreArchivo = "libros";
        boolean exito;
        System.out.println();
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
                System.out.println("1/3 Exportando a CSV...");
                if (ExportadorLibrosCSV.exportar(libros, nombreArchivo)) exitosos++;
                System.out.println("2/3 Exportando a XML...");
                if (ExportadorLibrosXML.exportar(libros, nombreArchivo)) exitosos++;
                System.out.println("3/3 Exportando a JSON...");
                if (ExportadorLibrosJSON.exportar(libros, nombreArchivo)) exitosos++;
                System.out.println("\nRESUMEN DE EXPORTACIÓN:");
                System.out.println("Formatos exportados: " + exitosos + "/3\n");
                exito = true;
                if (exitosos != 3) System.out.println("⚠ No se han podido crear todos los archivos.");
                break;
            case 0:
                System.out.println("Volviendo al menú principal...");
                return;
            default:
                opcionInvalida();
                return;
        }
        mostrarMensajeExito(exito);
    }

    private static void menuExportarReservasHotel() {
        int opcion = mostrarMenuElegirFormato();
        String nombreArchivo = "reservasHotel";
        boolean exito;
        System.out.println();
        switch (opcion) {
            case 1:
                exito = ExportadorReservasCSV.exportar(reservas, nombreArchivo);
                break;
            case 2:
                exito = ExportadorReservasXML.exportar(reservas, nombreArchivo);
                break;
            case 3:
                exito = ExportadorReservasJSON.exportar(reservas, nombreArchivo);
                break;
            case 4:
                int exitosos = 0;
                System.out.println("1/3 Exportando a CSV...");
                if (ExportadorReservasCSV.exportar(reservas, nombreArchivo)) exitosos++;
                System.out.println("2/3 Exportando a XML...");
                if (ExportadorReservasXML.exportar(reservas, nombreArchivo)) exitosos++;
                System.out.println("3/3 Exportando a JSON...");
                if (ExportadorReservasJSON.exportar(reservas, nombreArchivo)) exitosos++;
                System.out.println("\nRESUMEN DE EXPORTACIÓN:");
                System.out.println("Formatos exportados: " + exitosos + "/3\n");
                exito = true;
                if (exitosos != 3) System.out.println("⚠ No se han podido crear todos los archivos.");
                break;
            case 0:
                System.out.println("Volviendo al menú principal...");
                return;
            default:
                opcionInvalida();
                return;
        }
        mostrarMensajeExito(exito);
    }

    public static void mostrarMensajeExito(boolean exito) {
        if (exito) {
            System.out.println("¡EXPORTACIÓN COMPLETADA :)!");
            System.out.println("(Ubicación: exportaciones/)");
        } else {
            System.out.println("No se ha podido completar la exportación.");
        }
    }
}