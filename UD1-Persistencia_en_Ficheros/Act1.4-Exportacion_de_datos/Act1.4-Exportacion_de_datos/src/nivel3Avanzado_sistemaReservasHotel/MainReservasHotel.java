package nivel3Avanzado_sistemaReservasHotel;

import nivel3Avanzado_sistemaReservasHotel.modelo.Cliente;
import nivel3Avanzado_sistemaReservasHotel.modelo.Habitacion;
import nivel3Avanzado_sistemaReservasHotel.modelo.Reserva;

import nivel3Avanzado_sistemaReservasHotel.exportadores.ExportadorReservasCSV;
import nivel3Avanzado_sistemaReservasHotel.exportadores.ExportadorReservasXML;
import nivel3Avanzado_sistemaReservasHotel.exportadores.ExportadorReservasJSON;

import java.util.ArrayList;
import java.time.LocalDate;

import static utils.Utils.*;

public class MainReservasHotel {
    private final static ArrayList<Reserva> reservas = new ArrayList<>();

    public static void main(String[] args) {

        // Creo clientes de ejemplo
        Cliente cliente1 = new Cliente(1, "Marta García", "juan@email.com", "666111222");
        Cliente cliente2 = new Cliente(2, "María López", "maria@email.com", "666222333");
        Cliente cliente3 = new Cliente(3, "Miguel Sánchez", "pedro@email.com", "666333444");

        // Creo habitaciones de ejemplo
        Habitacion hab1 = new Habitacion(101, "Doble", 90.00, false);
        Habitacion hab2 = new Habitacion(205, "Suite", 200.00, true);
        Habitacion hab3 = new Habitacion(303, "Individual", 50.00, true);
        Habitacion hab4 = new Habitacion(404, "Doble", 80.00, true);
        Habitacion hab5 = new Habitacion(505, "Suite", 200.00, false);

        // Creo reservas de ejemplo
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
            System.out.println("1. Exportar a CSV");
            System.out.println("2. Exportar a XML");
            System.out.println("3. Exportar a JSON");
            System.out.println("4. Exportar a TODOS los formatos");
            System.out.println("0. Salir");
            int opcion = leerOpcion();

            System.out.println();
            boolean exito = false;
            String nombreArchivo = "reservas_hotel";

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
                    // Exportar CSV
                    System.out.println("1/3 Exportando a CSV...");
                    if (ExportadorReservasCSV.exportar(reservas, nombreArchivo)) {
                        exitosos++;
                    }
                    // Exportar XML
                    System.out.println("2/3 Exportando a XML...");
                    if (ExportadorReservasXML.exportar(reservas, nombreArchivo)) {
                        exitosos++;
                    }
                    // Exportar JSON
                    System.out.println("3/3 Exportando a JSON...");
                    if (ExportadorReservasJSON.exportar(reservas, nombreArchivo)) {
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
