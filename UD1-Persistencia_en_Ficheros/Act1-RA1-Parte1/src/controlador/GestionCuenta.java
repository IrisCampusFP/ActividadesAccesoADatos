package controlador;

import exportadores.*;
import modelo.Cliente;
import modelo.Cuenta;
import modelo.Movimiento;
import vista.Menus;

import java.io.*;
import java.util.ArrayList;

import static utils.Utils.*;

public class GestionCuenta {
    private final static String rutaCuenta = "datos" + File.separator + "cuenta.dat";
    private final static File archivoCuenta = new File(rutaCuenta);
    private Cuenta cuenta;
    private final Menus m = new Menus();

    public void iniciarPrograma() {
        System.out.println("································");
        System.out.println("·  GESTIÓN DE CUENTA BANCARIA  ·");
        System.out.println("································");

        // 1. Se comprueba si existe el archivo en el que se guardan los datos
        if (archivoCuenta.exists()) {
            // Si el archivo existe, se carga la cuenta bancaria desde él
            try {
                cuenta = Cuenta.cargarDatos(archivoCuenta);
                System.out.println("(Datos cargados correctamente ✔)");
            } catch (Exception e) {
                System.out.println("Error al cargar datos: " + e.getMessage());
                System.exit(1);
            }
        } else {
            // Si no existe, se crea una nueva cuenta
            System.out.println("No se han encontrado datos previos. Creando nueva cuenta...");

            // Pido los datos al cliente para crear una nueva cuenta
            System.out.print("Introduce tu nombre completo: ");
            String nombre = sc.nextLine().trim();
            System.out.print("Introduce tu DNI: ");
            String DNI = sc.nextLine().trim();
            DNI = verificarDNI(DNI);
            System.out.print("Introduce el número de cuenta: ");
            String numCuenta = sc.nextLine().trim();

            // Creo un objeto cliente con los datos
            Cliente cliente = new Cliente(nombre, DNI, numCuenta);
            // Creo una nueva cuenta asociada al cliente con saldo inicial 0
            cuenta = new Cuenta(cliente, 0);

            // Guardo los datos de la cuenta en el archivo
            if (cuenta.guardarDatos(archivoCuenta)) {
                System.out.println("✔ Cuenta creada correctamente :).");
            } else {
                System.out.println("✖ No se ha podido crear la cuenta (error al guardar los datos).");
                return;
            }
        }
       menuPrincipal();
    }

    public void menuPrincipal() {
        while (true) {
            int opcion = m.mostrarMenuPrincipal();
            System.out.println();
            switch (opcion) {
                case 1:
                    try {
                        realizarIngreso();
                    } catch (IOException e) {
                        System.out.println("Error al realizar el ingreso: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        realizarRetirada();
                    } catch (IOException e) {
                        System.out.println("Error al realizar la retirada: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("Saldo actual: " + cuenta.getSaldo() + "€");
                    break;
                case 4:
                    cuenta.mostrarListadoMovimientos();
                    break;
                case 5:
                    Cliente t = cuenta.getCliente();
                    System.out.println("··· DATOS DE LA CUENTA ···");
                    System.out.println("- Nombre: " + t.getNombre());
                    System.out.println("- DNI: " + t.getDni());
                    System.out.println("- Número de cuenta: " + t.getNumeroCuenta());
                    break;
                case 6:
                    submenuExportarMovimientos();
                    break;
                case 7:
                    submenuExportarCuenta();
                    break;
                case 8:
                    if (cuenta.guardarDatos(archivoCuenta)) {
                        System.out.println("Datos guardados correctamente.");
                    } else {
                        System.out.println("⚠ No se han podido guardar los datos.");
                        System.out.println("Estás segur@ de que quieres cerrar el programa?");
                        if (!preguntaSiNo()) {
                            System.out.println("Operación cerrar cancelada. Volviendo al menú principal...");
                            break;
                        }
                    }
                    System.out.println("Cerrando programa... ¡Hasta pronto!");
                    sc.close();
                    return;
                default:
                    opcionInvalida();
            }
            pausar();
        }
    }


    private void realizarIngreso() throws IOException {

        System.out.println("··· REALIZAR INGRESO ···");

        System.out.print("Introduce la cantidad a ingresar: ");
        double cantidad = leerCantidadPositiva();

        String concepto = pedirConcepto();

        if (cuenta.ingresarSaldo(cantidad, concepto)) {
            System.out.println("Ingreso realizado correctamente ✔. Nuevo saldo: " + cuenta.getSaldo() + "€");
            actualizarDatos();
        }
    }

    private void realizarRetirada() throws IOException {
        System.out.println("··· REALIZAR RETIRADA ···");

        System.out.print("Introduce la cantidad a retirar: ");
        double cantidad = leerCantidadPositiva();

        /* Si el cliente intenta retirar más dinero del que tiene en la tienda
         * se lo indico y cancelo el movimiento. */
        if (cantidad > cuenta.getSaldo()) {
            System.out.println("Saldo insuficiente. (Saldo actual " + cuenta.getSaldo() + "€)\n");
            return;
        }

        String concepto = pedirConcepto();

        if (cuenta.retirarSaldo(cantidad, concepto)) {
            System.out.println("Retirada realizada con éxito ✔. Nuevo saldo: " + cuenta.getSaldo() + "€");
            actualizarDatos();
        } else {
            System.out.println("No se pudo realizar la retirada.");
        }
    }

    // Actualizo los datos del archivo tras cada movimiento para evitar pérdida de información ante un cierre inesperado del programa.
    private void actualizarDatos() {
        if (cuenta.guardarDatos(archivoCuenta)) {
            System.out.println("(Datos actualizados correctamente)");
        } else {
            System.out.println("⚠ Error al actualizar los datos de la cuenta");
        }
    }

    public String pedirConcepto() {
        System.out.print("Introduce el concepto: ");
        return sc.nextLine().trim();
    }


    public void submenuExportarMovimientos() {
        String nombreArchivo = "movimientos";
        boolean exito;
        int opcion = m.mostrarSubmenuFormatos();
        System.out.println();
        switch (opcion) {
            case 1:
                exito = ExportadorMovimientosCSV.exportar(cuenta.getMovimientos(), nombreArchivo);
                break;
            case 2:
                exito = ExportadorMovimientosXML.exportar(cuenta.getMovimientos(), nombreArchivo);
                break;
            case 3:
                exito = ExportadorMovimientosJSON.exportar(cuenta.getMovimientos(), nombreArchivo);
                break;
            case 4:
                exito = exportarTodosFormatosMovimientos(cuenta.getMovimientos(), nombreArchivo);
                break;
            case 5:
                System.out.println("Volviendo al menú pricipal...");
                return;
            default:
                opcionInvalida();
                pausar();
                return;
        }
        if (exito) {
            System.out.println("¡EXPORTACIÓN COMPLETADA :)!");
            System.out.println("(Ubicación: exportaciones/)");
        } else {
            System.out.println("No se ha podido completar la exportación.");
        }
    }

    public boolean exportarTodosFormatosMovimientos(ArrayList<Movimiento> movimientos, String nombreArchivo) {
        int exitosos = 0;

        // Exportar CSV
        System.out.println("1/3 Exportando a CSV...");
        if (ExportadorMovimientosCSV.exportar(movimientos, nombreArchivo)) {
            exitosos++;
        }

        // Exportar XML
        System.out.println("2/3 Exportando a XML...");
        if (ExportadorMovimientosXML.exportar(movimientos, nombreArchivo)) {
            exitosos++;
        }

        // Exportar JSON
        System.out.println("3/3 Exportando a JSON...");
        if (ExportadorMovimientosJSON.exportar(movimientos, nombreArchivo)) {
            exitosos++;
        }

        // Resumen
        System.out.println("\nRESUMEN DE EXPORTACIÓN:");
        System.out.println("Formatos exportados: " + exitosos + "/3\n");

        if (exitosos == 3) {
            return true;
        } else {
            System.out.println("⚠ No se han podido crear todos los archivos.");
            return false;
        }
    }

    public void submenuExportarCuenta() {
        String nombreArchivo = "cuenta";
        boolean exito;
        int opcion = m.mostrarSubmenuFormatos();
        System.out.println();
        switch (opcion) {
            case 1:
                exito = ExportadorCuentaCSV.exportar(cuenta, nombreArchivo);
                break;
            case 2:
                exito = ExportadorCuentaXML.exportar(cuenta, nombreArchivo);
                break;
            case 3:
                exito = ExportadorCuentaJSON.exportar(cuenta, nombreArchivo);
                break;
            case 4:
                exito = exportarTodosFormatosCuenta(cuenta, nombreArchivo);
                break;
            case 5:
                System.out.println("Volviendo al menú pricipal...");
                return;
            default:
                System.out.println("Error: formato no válido.");
                pausar();
                return;
            }
        if (exito) {
            System.out.println("¡EXPORTACIÓN COMPLETADA :)!");
            System.out.println("(Ubicación: exportaciones/" + nombreArchivo + ")");
        } else {
            System.out.println("No se ha podido completar la exportación.");
        }
    }

    public boolean exportarTodosFormatosCuenta(Cuenta cuenta, String nombreArchivo) {
        int exitosos = 0;

        // Exportar CSV
        System.out.println("1/3 Exportando a CSV...");
        if (ExportadorCuentaCSV.exportar(cuenta, nombreArchivo)) {
            exitosos++;
        }

        // Exportar XML
        System.out.println("2/3 Exportando a XML...");
        if (ExportadorCuentaXML.exportar(cuenta, nombreArchivo)) {
            exitosos++;
        }

        // Exportar JSON
        System.out.println("3/3 Exportando a JSON...");
        if (ExportadorCuentaJSON.exportar(cuenta, nombreArchivo)) {
            exitosos++;
        }

        // Resumen
        System.out.println("\nRESUMEN DE EXPORTACIÓN:");
        System.out.println("Formatos exportados: " + exitosos + "/3\n");
        return true;
    }
}
