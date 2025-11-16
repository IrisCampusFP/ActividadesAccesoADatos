package utils;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;


public class Utils {
    public final static Scanner sc = new Scanner(System.in);

    // CONTROL DE ENTRADA DE USUARIO:

    // (evita recibir números en cadenas de texto o cadenas vacías)
    public static String leerString() {
        try {
            String stringEntrada = sc.nextLine().trim();

            // Validación para evitar cadenas vacías
            if (stringEntrada.isEmpty()) {
                System.out.print("ERROR: El texto no puede estar vacío. Vuelve a intentarlo: ");
                return leerString(); // Llamada recursiva para pedir la entrada nuevamente
            }

            // Validación para evitar números en el texto
            if (stringEntrada.matches(".*\\d.*")) { // Verifica si el texto contiene números
                System.out.print("ERROR: El texto no puede contener números. Vuelve a intentarlo: ");
                return leerString(); // Llamada recursiva para pedir la entrada nuevamente
            }

            // Si no contiene números, se devuelve el string
            return stringEntrada;
        } catch (Exception e) {
            System.out.print("Error al leer el texto. Inténtalo de nuevo: ");
            return leerString();
        }
    }

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
            sc.nextLine(); // Limpiar buffer
            System.out.print("Por favor, introduce un número: ");
            return leerDouble();
        }
    }

    public static BigDecimal leerBigDecimal() {
        try {
            BigDecimal num = sc.nextBigDecimal();
            sc.nextLine(); // Limpiar buffer
            return num;
        } catch (InputMismatchException e) {
            sc.nextLine(); // Limpiar buffer
            System.out.print("Por favor, introduce un número válido: ");
            return leerBigDecimal();
        }
    }

    public static LocalDate leerFecha() {
        try {
            String fechaRecibida = sc.nextLine().trim();

            // Se intenta parsear la fecha con el formato "yyyy-MM-dd"
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate fecha = LocalDate.parse(fechaRecibida, formato); // Se convierte la cadena a LocalDate
            return fecha;
        } catch (DateTimeParseException e) {
            // Si la fecha no es válida, se muestra el error y se vuelve a pedir la entrada
            System.out.print("Fecha no válida. Introduce una fecha válida (formato: Año-Mes-Día): ");
            return leerFecha();
        }
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

    // Verifica si el DNI obtenido contiene 8 números y 1 letra
    public static String verificarDNI(String DNI) {
        while (DNI == null | !DNI.matches("\\d{8}[A-Za-z]")) {
            System.out.println("DNI incorrecto. El DNI debe contener 8 números y 1 letra.");
            System.out.print("Vuelve a introducir el DNI: ");
            DNI = sc.nextLine().trim();
        }
        return DNI.toUpperCase();
    }


    // PARA MENÚS:

    public static int leerOpcion() {
        System.out.print("↳ Seleccione una opción: ");
        return leerEntero();
    }

    // (Pausa el menú hasta que el usuario presione ENTER)
    public static void pausar() {
        System.out.print("\nPresiona ENTER para continuar...");
        sc.nextLine();
    }

    public static void opcionInvalida() {
        System.out.println("Opción inválida. Introduce el número correspondiente a la opción que quieras seleccionar.");
    }


    // EXPORTACIÓN (FORMATO)

    // Mostrar los valores double con 2 decimales (%.2f) en formato inglés ('.' en vez de ',' para los decimales)
    public static String formatearDouble(Double valorDouble) {
        return String.format(Locale.ENGLISH, "%.2f", valorDouble);
    }

    // Escapar texto para CSV
    public static String escaparCSV(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        // Si contiene el separador, comillas o saltos de línea, debemos escapar
        if (texto.contains(";") || texto.contains("\"") || texto.contains("\n")) {
            // Duplicamos las comillas y encerramos todo entre comillas
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }

        return texto;
    }

    // Escapar texto para JSON
    public static String escaparJSON(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        // IMPORTANTE: El orden importa - escapar \ primero
        return texto.replace("\\", "\\\\")   // Barra invertida primero
                .replace("\"", "\\\"")    // Comillas dobles
                .replace("\n", "\\n")     // Nueva línea
                .replace("\r", "\\r")     // Retorno de carro
                .replace("\t", "\\t");    // Tabulador
    }

    // Escapar texto para XML
    public static String escaparXML(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }

        // (El orden importa - escapar & primero)
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }


    // ARCHIVOS

    // Crear un directorio si no existe
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


    // SERIALIZACIÓN Y DESERIALIZACIÓN

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
