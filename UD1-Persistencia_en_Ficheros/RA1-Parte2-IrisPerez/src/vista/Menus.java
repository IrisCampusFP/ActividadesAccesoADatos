package vista;

import static utils.Utils.leerOpcion;

public class Menus {
    public int mostrarMenuPrincipal() {
        System.out.println("\n······ MENÚ PRINCIPAL ······");
        System.out.println("1. Ingresar dinero");
        System.out.println("2. Retirar dinero");
        System.out.println("3. Consultar saldo");
        System.out.println("4. Consultar listado de movimientos");
        System.out.println("5. (Extra) Consultar datos");
        System.out.println("6. Exportar listado de movimientos (PARTE 2)");
        System.out.println("7. (Extra) Exportar todos los datos de la cuenta");
        System.out.println("8. SALIR");

        return leerOpcion();
    }

    public int mostrarSubmenuFormatos() {
        System.out.println("··· ESCOGE UN FORMATO ···");
        System.out.println("1. Exportar a CSV");
        System.out.println("2. Exportar a XML");
        System.out.println("3. Exportar a JSON");
        System.out.println("4. Exportar a TODOS los formatos");
        System.out.println("5. Volver al menú principal");
        return leerOpcion();
    }
}
