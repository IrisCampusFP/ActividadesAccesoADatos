import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Cuenta {
    Scanner sc;
    Cliente cliente;
    double saldo;
    ArrayList<Movimiento> movimientos;

    public Cuenta(Cliente cliente) {
        this.cliente = cliente;
        this.saldo = 0; // El saldo inicial de una cuenta es 0
        this.movimientos = new ArrayList<>(); // Se crea un ArrayList vacío en el que se guardarán los movimientos
    }

    public Cuenta() {
        this.cliente = null;
        this.saldo = 0;
        this.movimientos = new ArrayList<>();
        this.sc = new Scanner(System.in);
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public ArrayList<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(ArrayList<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }

    public void ingresarDinero() {
        System.out.println("\nINGRESAR DINERO");
        System.out.print("Introduce la cantidad de dinero que quieras ingresar: ");
        double ingreso = pedirDouble();

        if (ingreso < 0) {
            System.out.println("No puedes ingresar una cantidad negativa de dinero.");
        } else {
            saldo += ingreso;
            System.out.println("Dinero Ingresado correctamente.");
            System.out.println("Saldo actual: " + saldo);
        }
    }

    public void retirarDinero() {
        System.out.println("\nINGRESAR DINERO");
        System.out.print("Introduce la cantidad de dinero que quieras retirar: ");
        double retirada = pedirDouble();

        if (retirada > saldo) {
            System.out.println("No retirar más dinero del que tienes en la cuenta.");
            System.out.println("(Saldo actual " + saldo + ")");
        } else if (retirada < 0) {
            System.out.println("ERROR: Debes introducir la cantidad a retirar en positivo.");
            retirarDinero();
        } else {
            saldo -= retirada;
            System.out.println("Dinero retirado correctamente.");
            System.out.println("Saldo actual: " + saldo);
        }
    }

    public void mostrarInformacion() {
        System.out.println("CUENTA BANCARIA:");
        System.out.println("- Datos:");
        // Muestro los datos del cliente
        Cliente cliente = getCliente();
        cliente.toString();
        // Muestro el saldo de la cuenta
        double saldo = getSaldo();
        System.out.print("\n- Saldo de la cuenta: ");
        getSaldo();
        // Muestro los movimientos de la cuenta
        System.out.println("\nMOVIMIENTOS");
        ArrayList<Movimiento> movimientos = getMovimientos();
        for (Movimiento movimiento : movimientos) {
            movimiento.toString();
        }
    }

    public double pedirDouble() {
        try {
            double num = sc.nextInt();
            sc.nextLine(); // (Limpiar buffer)
            return num;
        } catch (InputMismatchException e) {
            System.out.print("Entrada no válida. Debes introducir un número entero: ");
            return pedirDouble();
        }
    }
}
