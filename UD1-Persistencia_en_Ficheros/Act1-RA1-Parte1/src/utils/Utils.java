package utils;

import java.io.*;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class Utils {
    public final static Scanner sc = new Scanner(System.in);

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

    public static int leerOpcion() {
        System.out.print("↳ Seleccione una opción: ");
        return leerEntero();
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

    public static double leerCantidadPositiva() {
        double cant = leerDouble();
        while (cant <= 0) {
            System.out.print("Por favor, introduce una cantidad positiva: ");
            cant = leerDouble();
        }
        return cant;
    }

    public static boolean preguntaSiNo() {
        System.out.println("1. Sí");
        System.out.println("2. No");
        int opcion = leerOpcion();
        if (opcion == 1) {
            return true;
        } else if (opcion == 2) {
            return false;
        } else {
            System.out.print("Opción no válida. Por favor, introduce 1 (Sí) o 2 (No): ");
            return preguntaSiNo();
        }
    }

    // Metodo que verifica si el DNI obtenido contiene 8 números y 1 letra
    public static String verificarDNI(String DNI) {
        while (DNI == null | !DNI.matches("\\d{8}[A-Za-z]")) {
            System.out.println("DNI incorrecto. El DNI debe contener 8 números y 1 letra.");
            System.out.print("Vuelve a introducir el DNI: ");
            DNI = sc.nextLine().trim();
        }
        return DNI.toUpperCase();
    }


    // Mostrar los valores double con 2 decimales (%.2f) en formato inglés ('.' en vez de ',' para los decimales)
    public static String formatearDouble(Double valorDouble) {
        return String.format(Locale.ENGLISH, "%.2f", valorDouble);
    }


    // Metodo que pausa el menú hasta que el usuario presione ENTER
    public static void pausar() {
        System.out.print("\nPresiona ENTER para continuar...");
        sc.nextLine();
    }

    public static void opcionInvalida() {
        System.out.println("Opción inválida. Introduce el número correspondiente a la opción que quieras seleccionar.");
    }

    // METODO QUE CREA UN DIRECTORIO SI NO EXISTE
    public static boolean crearDirectorio(String DIRECTORIO) {
        try {
            File directorio = new File(DIRECTORIO);
            // Si no existe el directorio
            if (!directorio.exists()) {
                return directorio.mkdirs(); // Se crea el directorio y devuelve true si se crea, false si no
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error al crear el directorio '" + DIRECTORIO + "': " + e.getMessage());
            return false;
        }
    }

    // MÉTODOS DE SERIALIZACIÓN Y DESERIALIZACIÓN

    public static boolean serializar(Object objeto, File archivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(objeto);
            return true;
        } catch (IOException e) {
            System.out.println("Error al serializar objeto '" + objeto + "': " + e.getMessage());
            return false;
        }
    }

    public static Object deserializar(File archivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al deserializar objeto " + e.getMessage());
            return null;
        }
    }
}
