package utils;

import java.io.*;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;


public class Utils {
    public final static Scanner sc = new Scanner(System.in);

    // CONTROL DE ENTRADA DE USUARIO:

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
}
