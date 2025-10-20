import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GestionBancaria {
    File archivoCuentas = new File("datos/cuentas.txt");
    File archivoMovimientos = new File("datos/movimientos.txt");
    private static Scanner sc = new Scanner(System.in);
    private Menus m = new Menus();
    GestionCuenta gc = new GestionCuenta();
    // Arrays para almacenar las cuentas (máximo 100)
    String[] dnis = new String[100];
    String[] nombres = new String[100];
    String[] numerosCuenta = new String[100];
    double[] saldos = new double[100];
    int numCuentas = 0;

    public File getArchivoCuentas() {
        return archivoCuentas;
    }

    public File getArchivoMovimientos() {
        return archivoMovimientos;
    }

    public void iniciarPrograma() throws IOException {
        System.out.println("···········································");
        System.out.println("·   SISTEMA DE GESTIÓN BANCARIA - MULTI   ·");
        System.out.println("···········································");


        // CREO LOS ARCHIVOS SI NO EXISTEN:

        // Creo el archivo datos/cuentas.txt si no existe
        if (!archivoCuentas.exists()) {
            archivoCuentas.getParentFile().mkdirs(); // Creo el directorio padre si no existe
            archivoCuentas.createNewFile(); // Creo el archivo
        }

        // Creo el archivo datos/movimientos.txt si no existe
        if (!archivoMovimientos.exists()) {
            archivoCuentas.getParentFile().mkdirs(); // Creo el directorio padre si no existe
            archivoMovimientos.createNewFile();
        }

        // CARGO TODAS LAS CUENTAS Y SUS DATOS CON SCANNER:
        cargarCuentas();

        System.out.println("✓ Sistema inicializado correctamente.");
        System.out.println("✓ Cargadas " + numCuentas + " cuentas desde el fichero.");

        menuPrincipal();
    }

    // Metodo que obtiene todos los datos de todas las cuentas con Scanner
    public void cargarCuentas() throws FileNotFoundException {
        Scanner sc = new Scanner(archivoCuentas);
        // Por cada línea (cuenta)
        while (sc.hasNextLine() && numCuentas < 100) {
            // Scanneo la línea
            String linea = sc.nextLine();
            // Divido la línea en partes obteniendo los campos (separados por ';', formato CSV)
            String[] partes = linea.split(";");
            // Guardo cada campo en el array correspondiente
            dnis[numCuentas] = partes[0];
            nombres[numCuentas] = partes[1];
            numerosCuenta[numCuentas] = partes[2];
            saldos[numCuentas] = Double.parseDouble(partes[3]);
            // Sumo 1 al número de cuentas
            numCuentas++;
        }
        sc.close();
    }

    public void menuPrincipal() throws IOException {
        int opcion;
        do {
            opcion = m.menuPrincipal();
            System.out.println();

            switch (opcion) {
                case 1:
                    listarCuentas();
                    break;
                case 2:
                    crearCuenta();
                    break;
                case 3:
                    seleccionarCuenta();
                    break;
                case 4:
                    verEstadisticas();
                    break;
                case 5:
                    System.out.println("¡Hasta pronto! (Cerrando programa...)");
                    sc.close(); // Cierro el Scanner
                    break;
            }
        } while (opcion != 5);
    }

    // Metodo que muestra el DNI, nombre y saldo actual de todos los titulares
    public void listarCuentas() throws IOException {
        System.out.println("--- LISTADO DE TODAS LAS CUENTAS --");
        Scanner sc = new Scanner(archivoCuentas);
        // Por cada cuenta (línea)
        while (sc.hasNextLine()) {
            // Scanneo
            String linea = sc.nextLine();
            // Separo sus campos
            String[] partes = linea.split(";");

            // Obtengo los valores y los guardo en variables
            int DNI = Integer.parseInt(partes[0]);
            String nombreTitular = partes[1];
            // (partes[2] es el número de cuenta)
            double saldo = Double.parseDouble(partes[3]);

            // Imprimo los datos
            System.out.println("DNI: " + DNI + " | TITULAR: " + nombreTitular + " | SALDO: " + saldo + " €");
        }
    }

    /* NOTA: cada titular (con DNI como Primary Key) solo podrá estar
     * asociado a una cuenta, por lo que no se permite crear dos cuentas
     * con el mismo DNI (un mismo Titular no podrá tener dos cuentas). */

    /* Para soportar varias cuentas por titular habría que modificar el
     * diseño. (IBAN como Primary Key en vez de DNI).*/

    // Metodo para crear una nueva cuenta
    public void crearCuenta() {
        System.out.println("--- CREAR CUENTA ---");

        // Solicito los datos del nuevo Titular (1 por cuenta)
        System.out.print("Introduce el nombre completo del titular: ");
        String nombre = sc.nextLine();
        System.out.print("Introduce el DNI: ");
        String DNI = sc.nextLine();
        System.out.print("Introduce el número de cuenta: ");
        String numCuenta = sc.nextLine();

        // Creo un objeto titular con los datos obtenidos
        Titular titular = new Titular(DNI, nombre, numCuenta);

        // Creo una nueva cuenta asociada al nuevo titular con saldo 0.
        Cuenta cuenta = new Cuenta(titular, 0);

        // Guardo los datos de la cuenta en el fichero:
        cuenta.guardarDatos();
    }

    // Metodo que solicita el DNI del Titular para seleccionar su cuenta
    public void seleccionarCuenta() {
        System.out.print("Introduce el DNI del titular de la cuenta: ");
        String DNI = sc.nextLine();

        // Obtengo la posición (nº de línea) de la cuenta en el archivo
        int posicion = encontrarCuenta(DNI);

        if (posicion == -1) {
            System.out.println("No se ha encontrado ninguna cuenta con ese DNI.");
        } else {
            // Obtengo los datos de la cuenta mediante su posición:
            Cuenta cuenta = obtenerCuentaPorPosicion(posicion);
            System.out.println("✓ Cuenta seleccionada: " + cuenta.getTitular().getNombre()); // o nombres[posicion]
            gc.menuOperaciones(cuenta);
        }
    }

    // Metodo que busca una cuenta mediante su DNI
    public int encontrarCuenta(String DNIBuscado) {
        // Busco el DNI en el array 'dnis'
        for (int i = 0; i < numCuentas; i++) {
            if (dnis[i].equals(DNIBuscado)) {
                return i; // Devuelve la posición (nº de línea) si encuentra coincidencia
            }
        }
        return -1; // Devuelve -1 si no encuentra ninguna coincidencia
    }

    // Metodo que obtiene los datos de una cuenta según su posición (nº de línea)
    public Cuenta obtenerCuentaPorPosicion(int posicion) {
        Titular titular = new Titular(dnis[posicion], nombres[posicion], numerosCuenta[posicion]);
        return new Cuenta(titular, saldos[posicion]);
    }


    // Metodo que muestra el número total de cuentas, saldo total del banco, etc.
    public void verEstadisticas() {
        System.out.println("--- ESTADÍSTICAS GENERALES ---");

        System.out.println("- Número total de cuentas: " + numCuentas);

        // Calculo el saldo total del banco sumando todos los valores del array saldos[]
        double saldoTotal = 0;
        for (double saldo : saldos) {
            saldoTotal += saldo;
        }
        System.out.println("- Saldo total del banco: " + saldoTotal);

        // Calculo el saldo medio por cuenta
        double saldoPromedio = saldoTotal/numCuentas;
        System.out.println("- Saldo medio por cuenta: " + saldoPromedio);

        // Muestro el número total de ingresos y retiradas
        int ingresos = 0, retiradas = 0;
        try {
            Scanner sc = new Scanner(archivoMovimientos);
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                String[] partes = linea.split(";");
                String tipo = partes[1];
                if (tipo.equalsIgnoreCase("INGRESO")) {
                    ingresos++;
                } else if (tipo.equalsIgnoreCase("RETIRADA")) {
                    retiradas++;
                }
            }
            sc.close();
            System.out.println("- Número total de ingresos: " + ingresos);
            System.out.println("- Número total de retiradas: " + retiradas);
        } catch (FileNotFoundException e) {
            System.out.println("No se encontró el fichero de movimientos.");
        }

        // SOLO SI EXISTEN CUENTAS CREADAS
        if (numCuentas > 0) {
            // Muestro los datos del titular con MENOS saldo de todos
            System.out.print("- Titular con menor saldo: ");
            int posMenorSaldo = 0;
            // Busco la posición del menor saldo
            for (int i = 0; i < numCuentas; i++) {
                if (saldos[i] < saldos[posMenorSaldo]) posMenorSaldo = i;
            }
            // Obtengo los datos de la cuenta
            Cuenta cuentaMayorSaldo = obtenerCuentaPorPosicion(posMenorSaldo);
            System.out.println(cuentaMayorSaldo.getTitular().getNombre() + ", DNI: " + cuentaMayorSaldo.getTitular().getDni() + " (SALDO: " + cuentaMayorSaldo.getSaldo() + ")");

            // Muestro los datos del titular con MÁS saldo de todos
            System.out.print("- Titular con mayor saldo: ");
            int posMayorSaldo = 0;
            // Busco la posición del mayor saldo
            for (int i = 0; i < numCuentas; i++) {
                if (saldos[i] > saldos[posMayorSaldo]) posMayorSaldo = i;
            }
            // Obtengo los datos de la cuenta
            Cuenta cuentaMenorSaldo = obtenerCuentaPorPosicion(posMenorSaldo);
            System.out.println(cuentaMenorSaldo.getTitular().getNombre() + ", DNI: " + cuentaMenorSaldo.getTitular().getDni() + " (SALDO: " + cuentaMenorSaldo.getSaldo() + ")");
        }
    }


    // MÉTODOS DE CONTROL DE ENTRADA DE USUARIO:

    public static int leerEntero() {
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

    public static double leerDouble() {
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