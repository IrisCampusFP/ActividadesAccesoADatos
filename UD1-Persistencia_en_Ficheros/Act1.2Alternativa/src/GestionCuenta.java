import java.io.IOException;
import java.util.InputMismatchException;

public class GestionCuenta {
    Menus m = new Menus();
    Cuenta cuenta;

    public void menuOperaciones(Cuenta cuenta) {
        this.cuenta = cuenta;

        int opcion;
        do {
            opcion = m.menuOperaciones(cuenta.getTitular().getNombre());
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
                    break;
                default:
                    System.out.println("Opción no válida. Introduce el número correspondiente a la opción que quieras seleccionar.");
                    menuPrincipal();
                    break;
            }
        } while (opcion != 6);
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
}
