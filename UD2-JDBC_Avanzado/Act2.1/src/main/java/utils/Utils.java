package utils;

import java.io.*;
import java.util.InputMismatchException;
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
}
