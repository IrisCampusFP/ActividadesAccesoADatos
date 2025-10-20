import java.io.*;

public class Cuenta {
    GestionBancaria gb = new GestionBancaria();
    private Titular titular; // (DNI, nombre y numeroCuenta)
    private double saldo;

    public Cuenta(Titular titular, double saldo) {
        this.titular = titular;
        this.saldo = saldo;
    }

    public Titular getTitular() { return titular; }
    public double getSaldo() { return saldo; }

    // Metodo que guarda los datos de una nueva cuenta en el archivo
    public void guardarDatos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(gb.getArchivoCuentas()))) {
            bw.write(titular.getDni() + ";" + titular.getNombre() + ";" + titular.getNumeroCuenta() + ";" + saldo);
            bw.newLine();
            System.out.println("Cuenta creada correctamente :)");
        } catch (IOException e) {
            System.out.println("Error al guardar los datos de la cuenta: " + e.getMessage());
        }
    }

    // Metodo que muestra SOLO los movimientos del titular de la cuenta
    public void mostrarHistorial() {
        System.out.println("HISTORIAL DE MOVIMIENTOS:");

        // [LEER LOS MOVIMIENTOS DE X DNI USANDO SCANNER]
        // gb.archivoMovimientos
        if (archivo.length() == 0) {
            System.out.println("No hay movimientos registrados.");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(rutaMovimientos))) {
            String linea;
            // Por cada línea del archivo (movimiento), obtengo sus campos y los muestro con toString
            while ((linea = br.readLine()) != null) {
                Movimiento m = Movimiento.obtenerMovimientos(linea);
                System.out.println(m.toString());
            }
        } catch (IOException e) {
            System.out.println("Error al leer el historial de movimientos: " + e.getMessage());
        }
    }

    // Metodo que realiza un ingreso de saldo
    public boolean ingresarSaldo(double cantidad, String concepto) throws IOException {
        if (cantidad <= 0) return false; // Si la cantidad es negativa no se realiza el ingreso
        saldo += cantidad;
        guardarSaldo(); // Actualizo el saldo en el archivo

        // Registro el movimiento en el archivo de movimientos
        Movimiento m = new Movimiento("INGRESO", cantidad, concepto);
        m.guardarMovimiento(rutaMovimientos);
        return true;
    }

    // Metodo que realiza una retirada de saldo
    public boolean retirarSaldo(double cantidad, String concepto) throws IOException {
        if (cantidad <= 0 || cantidad > saldo) return false; // Si la cantidad a retirar es negativa o superior al saldo de la cuenta no se realiza la operación
        saldo -= cantidad;
        guardarSaldo(); // Actualizo el saldo en el archivo

        // Registro el movimiento en el archivo correspondiente
        Movimiento m = new Movimiento("RETIRADA", cantidad, concepto);
        m.guardarMovimiento(rutaMovimientos);
        return true;
    }



    public static Cuenta cargarDatos(String rutaTitular, String rutaSaldo, String rutaMovimientos) throws IOException {
        // Obtengo los datos del titular desde su archivo
        Titular titular = Titular.obtenerDatos(rutaTitular);
        // Leo el saldo desde su archivo
        double saldo;
        try (BufferedReader br = new BufferedReader(new FileReader(rutaSaldo))) {
            saldo = Double.parseDouble(br.readLine()); // Convierto el texto obtenido del archivo (saldo) a double
        } catch (NumberFormatException e) { // Se lanza una excepción si el saldo guardado no estuviera en el formato adecuado (double)
            throw new IOException("Error al obtener el saldo: " + e.getMessage());
        }
        return new Cuenta(titular, saldo, rutaTitular, rutaSaldo, rutaMovimientos);
    }
}