import java.io.*;

public class Titular {
    private String DNI;
    private String nombre;
    private String numeroCuenta;

    public Titular(String DNI, String nombre, String numeroCuenta) {
        this.DNI = DNI;
        this.nombre = nombre;
        this.numeroCuenta = numeroCuenta;
    }

    public String getDni() { return DNI; }
    public String getNombre() { return nombre; }
    public String getNumeroCuenta() { return numeroCuenta; }

//    YA NO SE USA
//    public void guardarDatos(String ruta) throws IOException {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, false))) {
//            bw.write(DNI); bw.newLine();
//            bw.write(nombre); bw.newLine();
//            bw.write(numeroCuenta); bw.newLine();
//        }
//    }

    public static Titular obtenerDatos(String ruta) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String dni = br.readLine();
            String nombre = br.readLine();
            String numeroCuenta = br.readLine();
            return new Titular(dni, nombre, numeroCuenta);
        }
    }
}