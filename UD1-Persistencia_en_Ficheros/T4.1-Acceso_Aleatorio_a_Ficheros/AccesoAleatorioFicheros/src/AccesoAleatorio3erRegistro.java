import java.io.*;

public class AccesoAleatorio3erRegistro {
    public static void main(String[] args) {
        try {
            // Escribir registros
            RandomAccessFile raf = new RandomAccessFile("registros.dat", "rw");
            raf.writeUTF("Registro 1");
            raf.writeUTF("Registro 2");
            raf.writeUTF("Registro 3");

            // Calculo la posición del tercer registro
            raf.seek(0);
            raf.readUTF(); // Salta el primer registro
            raf.readUTF(); // Salta el segundo registro
            long pos3erRegistro = raf.getFilePointer();

            // Muevo el puntero a la posición del 3er registro
            raf.seek(pos3erRegistro);
            // Modifico el registro
            raf.writeUTF("MODIFICACIÓN 3er REGISTRO");

            // Compruebo el resultado
            raf.seek(0); // Muevo el puntero al inicio del archivo
            // Leo y muestro los 3 registros
            System.out.println(raf.readUTF()); // Registro 1
            System.out.println(raf.readUTF()); // Registro 2
            System.out.println(raf.readUTF()); // Registro 3

            raf.close(); // Cierro el puntero
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}