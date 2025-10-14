import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GestionCuenta {
    private String rutaTitular = "datos/titular.txt";
    private String rutaSaldo = "datos/saldo.txt";
    private String rutaMovimientos = "datos/movimientos.txt";
    private CuentaBancaria cuenta;
    private Scanner sc = new Scanner(System.in);


    public void iniciarPrograma() {
        System.out.println("GESTIÓN DE CUENTA BANCARIA:");

        File archivoTitular = new File(rutaTitular);
        File archivoSaldo = new File(rutaSaldo);
        File archivoMovimientos = new File(rutaMovimientos);

        // Si los archivos existen, se cargan los datos
        if (archivoTitular.exists() && archivoSaldo.exists() && archivoMovimientos.exists()) {
            try {
                cuenta = CuentaBancaria.cargarDatos(rutaTitular, rutaSaldo, rutaMovimientos);
                System.out.println("Datos cargados correctamente.");
            } catch (Exception e) {
                System.out.println("Error al cargar datos: " + e.getMessage());
                System.exit(1);
            }
        } else {
            System.out.println("No se han encontrado datos previos. Creando nueva cuenta...");

            // Se pide al usuario los datos del titular para crear una nueva cuenta
            System.out.print("Introduce el nombre completo del titular: ");
            String nombreTitular = sc.nextLine();
            System.out.print("Introduce el DNI: ");
            String dniTitular = sc.nextLine();
            System.out.print("Introduce el número de cuenta: ");
            String numCuenta = sc.nextLine();

            // Creo un objeto titular con los datos
            Titular titular = new Titular(nombreTitular, dniTitular, numCuenta);
            // Creo una nueva cuenta asociada al titular con saldo 0 (paso las rutas de los ficheros en los que se guardan los datos)
            cuenta = new CuentaBancaria(titular, 0, rutaTitular, rutaSaldo, rutaMovimientos);


            // GUARDO LOS DATOS DE LA CUENTA Y SU TITULAR EN LOS FICHEROS:

            // Si no existe la carpeta datos, se crea
            File carpetaDatos = new File("datos");
            if (!carpetaDatos.exists()) {
                carpetaDatos.mkdir();
            }

            try {
                titular.guardarDatos(rutaTitular); // Guardo los datos del titular en su fichero
                cuenta.guardarSaldo(); // Guardo la cantidad de saldo en su fichero (0)
                new FileWriter(rutaMovimientos).close(); // Creo o sobrescribo el fichero de movimientos dejándolo vacío (y cierro el FileWriter para liberar memoria)
                System.out.println("Cuenta creada correctamente con saldo inicial: " + cuenta.getSaldo() + "€");
            } catch (IOException e) {
                System.out.println("Error en la creación de un fichero: " + e.getMessage());
                System.exit(1);
            }
        }
       menuPrincipal();
    }


    public void menuPrincipal() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\nMENÚ PRINCIPAL");
            System.out.println("1. Consultar saldo");
            System.out.println("2. Consultar datos del titular");
            System.out.println("3. Ver historial de movimientos");
            System.out.println("4. Realizar ingreso");
            System.out.println("5. Realizar retirada");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = leerEntero();
            System.out.println();
            switch (opcion) {
                case 1:
                    System.out.println("Saldo actual: " + cuenta.getSaldo() + "€");
                    break;
                case 2:
                    Titular t = cuenta.getTitular();
                    System.out.println("DATOS DEL TITULAR:");
                    System.out.println("- Nombre: " + t.getNombre());
                    System.out.println("- DNI: " + t.getDni());
                    System.out.println("- Número de cuenta: " + t.getNumeroCuenta());
                    break;
                case 3:
                    cuenta.mostrarHistorial();
                    break;
                case 4:
                    realizarIngreso();
                    break;
                case 5:
                    realizarRetirada();
                    break;
                case 6:
                    try {
                        cuenta.guardarTodo();
                    } catch (IOException e) {
                        System.out.println("Error al guardar los datos: " + e.getMessage());
                    }
                    System.out.println("Cerrando programa... ¡Hasta pronto!");
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida. Introduce el número correspondiente a la opción que quieras seleccionar.");
                    menuPrincipal();
                    break;
            }
        }
        sc.close();
    }


    private void realizarIngreso() {
        System.out.println("REALIZAR INGRESO");
        try {
            System.out.print("Introduce la cantidad a ingresar: ");
            double cantidad = leerDouble();
            if (cantidad <= 0) {
                System.out.println("Error: La cantidad ingresada no puede ser negativa.");
                return;
            }
            System.out.print("Introduce el concepto: ");
            String concepto = sc.nextLine();
            if (cuenta.ingresarSaldo(cantidad, concepto)) {
                System.out.println("\nIngreso realizado correctamente :). Nuevo saldo: " + cuenta.getSaldo() + "€");
            } else {
                System.out.println("Error: no se ha podido realizar el ingreso.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Cantidad no válida.");
        } catch (IOException e) {
            System.out.println("Error al guardar el movimiento (ingreso): " + e.getMessage());
        }
    }

    private void realizarRetirada() {
        System.out.println("REALIZAR RETIRADA:");
        try {
            System.out.print("Introduce la cantidad a retirar: ");
            double cantidad = leerDouble();
            if (cantidad <= 0) {
                System.out.println("Error: La cantidad debe ser positiva.");
                return;
            }
            if (cantidad > cuenta.getSaldo()) {
                System.out.println("Saldo insuficiente. (Saldo actual " + cuenta.getSaldo() + "€)\n");
                return;
            }
            System.out.print("Introduce el concepto: ");
            String concepto = sc.nextLine();
            if (cuenta.retirarSaldo(cantidad, concepto)) {
                System.out.println("Retirada realizada correctamente :). Nuevo saldo: " + cuenta.getSaldo() + "€");
            } else {
                System.out.println("Error: No se pudo realizar la retirada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Cantidad no válida.");
        } catch (IOException e) {
            System.out.println("Error al guardar el movimiento (retirada): " + e.getMessage());
        }
    }


    public int leerEntero() {
        try {
            int num = sc.nextInt();
            sc.nextLine(); // Limpiar buffer
            return num;
        } catch (InputMismatchException e) {
            sc.nextLine(); // Limpiar buffer
            System.out.print("Por favor, introduce un número entero: ");
            return leerEntero();
        }
    }

    public double leerDouble() {
        try {
            double num = sc.nextDouble();
            sc.nextLine(); // Limpiar buffer
            return num;
        } catch (InputMismatchException e) {
            sc.nextDouble(); // Limpiar buffer
            System.out.print("Por favor, introduce un número: ");
            return leerDouble();
        }
    }
}