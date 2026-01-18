package com.dam.accesodatos.loginv2_irisperez.utils;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;


public class LoginUtils {
    public final static Scanner sc = new Scanner(System.in);

    /* ·································
     *  ENTRADA DE USUARIO
     * ································· */

    // Evita recibir números en cadenas de texto o cadenas vacías
    public static String leerString() {
        try {
            String stringEntrada = sc.nextLine().trim();

            // evitar cadenas vacías
            if (stringEntrada.isEmpty()) {
                System.out.print("ERROR: El texto no puede estar vacío. Vuelve a intentarlo: ");
                return leerString(); // Llamada recursiva para pedir la entrada nuevamente
            }

            // evitar números en el texto
            if (stringEntrada.matches(".*\\d.*")) { // Verifica si el texto contiene números
                System.out.print("ERROR: El texto no puede contener números. Vuelve a intentarlo: ");
                return leerString(); // Llamada recursiva para pedir la entrada nuevamente
            }

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

    public static Long leerLong() {
        try {
            Long num = sc.nextLong();
            sc.nextLine(); // Limpiar buffer
            return num;
        } catch (InputMismatchException e) {
            sc.nextLine(); // Limpiar buffer
            System.out.print("Por favor, introduce un número válido: ");
            return leerLong();
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

    public static String leerEmail() {
        try {
            String email = sc.nextLine().trim().toLowerCase();

            // Evitar cadena vacía
            if (email.isEmpty()) {
                System.out.print("ERROR: El email no puede estar vacío. Vuelve a intentarlo: ");
                return leerEmail();
            }

            /*
             * Validación real basada en RFC 5322 (versión práctica).
             * Acepta:
             * - letras, números y símbolos válidos
             * - subdominios
             * - dominios correctos
             */
            String regexEmail =
                    "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+" +
                            "@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

            if (!email.matches(regexEmail)) {
                System.out.print("ERROR: Email no válido (formato incorrecto). Vuelve a intentarlo: ");
                return leerEmail();
            }

            return email;

        } catch (Exception e) {
            System.out.print("Error al leer el email. Inténtalo de nuevo: ");
            return leerEmail();
        }
    }

    public static String leerDNI() {
        try {
            String dni = sc.nextLine().trim().toUpperCase();

            // Evitar cadena vacía
            if (dni.isEmpty()) {
                System.out.print("ERROR: El DNI no puede estar vacío. Vuelve a intentarlo: ");
                return leerDNI();
            }

            // Formato DNI español: 8 números + 1 letra
            if (!dni.matches("\\d{8}[A-Z]")) {
                System.out.print("ERROR: Formato de DNI inválido (ejemplo: 12345678A). Vuelve a intentarlo: ");
                return leerDNI();
            }

            // Validación de letra
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            int numero = Integer.parseInt(dni.substring(0, 8));
            char letraCorrecta = letras.charAt(numero % 23);

            if (dni.charAt(8) != letraCorrecta) {
                System.out.print("ERROR: La letra del DNI no es correcta. Vuelve a intentarlo: ");
                return leerDNI();
            }

            return dni;

        } catch (Exception e) {
            System.out.print("Error al leer el DNI. Inténtalo de nuevo: ");
            return leerDNI();
        }
    }


    /* ·································
     *  MENÚS
     * ································· */
    public static int leerOpcion() {
        System.out.print("↳ Seleccione una opción: ");
        return leerEntero();
    }

    // Pausa el menú hasta que el usuario presione ENTER
    public static void pausar() {
        System.out.print("\nPresiona ENTER para continuar...");
        sc.nextLine();
    }

    public static void mensajeOpcionInvalida() {
        System.out.println("Opción inválida. Introduce el número correspondiente a la opción que quieras seleccionar.");
    }
}