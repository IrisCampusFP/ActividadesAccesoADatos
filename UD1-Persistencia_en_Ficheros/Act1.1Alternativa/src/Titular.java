import java.io.*;

public class Titular {
    private String nombre;
    private String DNI;
    private String numeroCuenta;

    public Titular(String nombre, String DNI, String numeroCuenta) {
        this.nombre = nombre;
        this.DNI = DNI;
        this.numeroCuenta = numeroCuenta;
    }

    public String getNombre() { return nombre; }
    public String getDni() { return DNI; }
    public String getNumeroCuenta() { return numeroCuenta; }

    public void guardarDatos(String ruta) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, false))) {
            bw.write(nombre); bw.newLine();
            bw.write(DNI); bw.newLine();
            bw.write(numeroCuenta); bw.newLine();
        }
    }

    public static Titular obtenerDatos(String ruta) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String nombre = br.readLine();
            String dni = br.readLine();
            String numeroCuenta = br.readLine();
            return new Titular(nombre, dni, numeroCuenta);
        }
    }
}