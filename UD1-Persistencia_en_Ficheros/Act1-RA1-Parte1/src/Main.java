import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GestionarCuenta gc = new GestionarCuenta();
        Scanner scanner = new Scanner(System.in);
        try {
            File archivo = new File("datos/cuenta.dat");
            Cuenta cuenta = new Cuenta();

            // Si el archivo 'cuenta.dat' no existe, se crea
            if (!archivo.exists()) {
                // Creo el archivo y su directorio si no existe
                archivo.getParentFile().mkdirs();
                archivo.createNewFile();
                
                // Pregunto el nombre al usuario (cliente)
                System.out.print("Introduce tu nombre: ");
                String nombre = scanner.nextLine();
                // Creo un objeto cliente y lo asocio a la cuenta
                cuenta.cliente = new Cliente(nombre);

                // Serializo el objeto cuenta y lo guardo en el archivo
                gc.serializar(cuenta, archivo);
                return;
            } else { // Si existe, se carga la cuenta bancaria
                // Obtengo los datos de la cuenta deserializando el archivo
                cuenta = gc.deserializar(archivo);

                if (cuenta == null) {
                    System.out.println("Error al obtener los datos de la cuenta.");
                    return;
                }
            }

            // Muestro al usuario el menu para realizar operaciones
            gc.menu(cuenta);

            // Al final de la ejecución del programa, el objeto cuenta se serializa y se guarda en el archivo
            gc.serializar(cuenta, archivo);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
