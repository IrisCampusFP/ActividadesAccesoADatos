import java.io.*;

public class CuentaBancaria {
    private Titular titular;
    private double saldo;
    private String rutaTitular, rutaSaldo, rutaMovimientos;

    public CuentaBancaria(Titular titular, double saldo, String rutaTitular, String rutaSaldo, String rutaMovimientos) {
        this.titular = titular;
        this.saldo = saldo;
        this.rutaTitular = rutaTitular;
        this.rutaSaldo = rutaSaldo;
        this.rutaMovimientos = rutaMovimientos;
    }

    public Titular getTitular() { return titular; }
    public double getSaldo() { return saldo; }

    public void guardarTodo() throws IOException {
        titular.guardarDatos(rutaTitular);
        guardarSaldo();
    }

    public void guardarSaldo() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaSaldo))) {
            bw.write(String.valueOf(saldo)); // (Convierto el saldo de double a String para escribirlo en el fichero de texto)
        }
    }

    public boolean ingresarSaldo(double cantidad, String concepto) throws IOException {
        if (cantidad <= 0) return false; // Si la cantidad es negativa no se realiza el ingreso
        saldo += cantidad;
        guardarSaldo(); // Actualizo el saldo en el archivo

        // Registro el movimiento en el archivo de movimientos
        Movimiento m = new Movimiento("INGRESO", cantidad, concepto);
        m.guardarMovimiento(rutaMovimientos);
        return true;
    }

    public boolean retirarSaldo(double cantidad, String concepto) throws IOException {
        if (cantidad <= 0 || cantidad > saldo) return false; // Si la cantidad a retirar es negativa o superior al saldo de la cuenta no se realiza la operación
        saldo -= cantidad;
        guardarSaldo(); // Actualizo el saldo en el archivo

        // Registro el movimiento en el archivo correspondiente
        Movimiento m = new Movimiento("RETIRADA", cantidad, concepto);
        m.guardarMovimiento(rutaMovimientos);
        return true;
    }

    public void mostrarHistorial() {
        System.out.println("HISTORIAL DE MOVIMIENTOS:");
        File archivo = new File(rutaMovimientos);
        if (archivo.length() == 0) {
            System.out.println("No hay movimientos registrados.");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(rutaMovimientos))) {
            String linea;
            // Por cada línea del archivo (movimiento), obtengo sus campos y los muestro con toString
            while ((linea = br.readLine()) != null) {
                Movimiento m = Movimiento.parse(linea);
                System.out.println(m.toString());
            }
        } catch (IOException e) {
            System.out.println("Error al leer el historial de movimientos: " + e.getMessage());
        }
    }

    public static CuentaBancaria cargarDatos(String rutaTitular, String rutaSaldo, String rutaMovimientos) throws IOException {
        // Obtengo los datos del titular desde su archivo
        Titular titular = Titular.obtenerDatos(rutaTitular);
        // Leo el saldo desde su archivo
        double saldo;
        try (BufferedReader br = new BufferedReader(new FileReader(rutaSaldo))) {
            saldo = Double.parseDouble(br.readLine()); // Convierto el texto obtenido del archivo (saldo) a double
        } catch (NumberFormatException e) { // Se lanza una excepción si el saldo guardado no estuviera en el formato adecuado (double)
            throw new IOException("Error al obtener el saldo: " + e.getMessage());
        }
        return new CuentaBancaria(titular, saldo, rutaTitular, rutaSaldo, rutaMovimientos);
    }
}