package modelo;

import java.io.*;
import java.util.ArrayList;
import static utils.Utils.*;

public class Cuenta implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Cliente cliente;
    private final ArrayList<Movimiento> movimientos = new ArrayList<>();
    private double saldo;

    public Cuenta(Cliente cliente, double saldo) {
        this.cliente = cliente;
        this.saldo = saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public ArrayList<Movimiento> getMovimientos() {
        return movimientos;
    }

    // Getters y Setters
    public double getSaldo() {
        return saldo;
    }


    /* Guarda los datos de la cuenta en el archivo recibido
     * utilizando el metodo serializar de la clase Utils. */
    public boolean guardarDatos(File archivo) {
        // Si no existe el archivo o su directorio, se crea
        if (!archivo.exists()) {
            archivo.getParentFile().mkdirs(); // Crea el directorio padre si no existe
            try {
                archivo.createNewFile(); // Crea el archivo
            } catch (IOException e) {
                System.out.println("Error al crear el archivo '" + archivo + "': " + e.getMessage());
                return false;
            }
        }

        // Serializo y guardo los datos del objeto cuenta en el archivo
        return serializar(this, archivo); // Devuelve true si se serializa, false si no
    }

    // Ingresa la cantidad de saldo recibida en la cuenta
    public boolean ingresarSaldo(double cantidad, String concepto) {
        if (cantidad <= 0) return false; // Si la cantidad es negativa no se realiza el ingreso
        saldo += cantidad;

        registrarMovimiento("Ingreso", cantidad, concepto);
        return true;
    }

    // Retira la cantidad de saldo recibida
    public boolean retirarSaldo(double cantidad, String concepto) {
        if (cantidad <= 0 || cantidad > saldo) return false; // Si la cantidad a retirar es negativa o superior al saldo de la cuenta no se realiza la operación
        saldo -= cantidad;

        // Registro el movimiento realizado (ingreso)
        registrarMovimiento("Retirada", cantidad, concepto);
        return true;
    }

    // Registra un movimiento en el ArrayList
    public void registrarMovimiento(String tipo, double cantidad, String concepto) {
        movimientos.add(new Movimiento(tipo, cantidad, concepto));
    }

    public void mostrarListadoMovimientos() {
        System.out.println("··· LISTADO DE MOVIMIENTOS ···");
        for (Movimiento m : movimientos) {
            System.out.println(m);
        }
        if (movimientos.isEmpty()) {
            System.out.println("No hay movimientos registrados.");
        }
    }

    public static Cuenta cargarDatos(File archivoCuenta) {
        // Obtengo los datos de la cuenta deserializando el archivo
        return (Cuenta) deserializar(archivoCuenta);
    }
}