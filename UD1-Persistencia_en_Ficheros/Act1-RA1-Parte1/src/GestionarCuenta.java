import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GestionarCuenta {
    Scanner sc;

    public GestionarCuenta() {
        sc = new Scanner(System.in);
    }

    public void serializar(Cuenta cuenta, File archivo) throws IOException {
        // Si no existe el archivo, se crea
        if (!archivo.exists()) {
            archivo.getParentFile().mkdirs();
            archivo.createNewFile();
        } else {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
                oos.writeObject(cuenta);
                System.out.println("Datos de la cuenta actualizados correctamente.");
            } catch (IOException e) {
                System.out.println("Error al actualizar los datos de la cuenta: " + e.getMessage());
            }
        }
    }

    public Cuenta deserializar(File archivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            Cuenta cuenta = (Cuenta) ois.readObject();
            return cuenta;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al leer objeto: " + e.getMessage());
        }
        return null;
    }

    public void menu(Cuenta c) {
        int opcion;
        do {
            System.out.println("MENU");
            System.out.println("1. Ingresar dinero.");
            System.out.println("2. Retirar dinero.");
            System.out.println("3. Consultar saldo y listado de movimientos.");
            System.out.println("4. SALIR.");
            opcion = pedirInt();

            switch (opcion) {
                case 1:
                    c.ingresarDinero();
                    break;
                case 2:
                    c.retirarDinero();
                    break;
                case 3:
                    c.mostrarInformacion();
            }
        } while (opcion != 4);
    }

    public int pedirInt() {
        try {
            int num = sc.nextInt();
            sc.nextLine(); // (Limpiar buffer)
            return num;
        } catch (InputMismatchException e) {
            System.out.print("Entrada no válida. Debes introducir un número entero: ");
            return pedirInt();
        }
    }
}
