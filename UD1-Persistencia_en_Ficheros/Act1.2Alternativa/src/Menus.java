public class Menus {
    public static int leerOpcion() {
        System.out.print("↳ Seleccione una opción: ");
        int opcion = GestionBancaria.leerEntero();
        return opcion;
    }

    public int menuPrincipal() {
        System.out.println("\n######### MENÚ PRINCIPAL #########");
        System.out.println("1. Listar todas las cuentas.");
        System.out.println("2. Crear nueva cuenta.");
        System.out.println("3. Seleccionar cuenta para operar.");
        System.out.println("4. Ver estadísticas generales.");
        System.out.println("5. Salir");
        return leerOpcion();
    }

    public int menuOperaciones(String nombreTitular) {
        System.out.println("\n### OPERACIONES - " + nombreTitular + " ###");
        System.out.println("1. Consultar saldo.");
        System.out.println("2. Ver datos del titular.");
        System.out.println("3. Ver historial de movimientos.");
        System.out.println("4. Realizar ingreso.");
        System.out.println("5. Realizar retirada.");
        System.out.println("6. Volver al menú principal.");
        return leerOpcion();
    }
}
