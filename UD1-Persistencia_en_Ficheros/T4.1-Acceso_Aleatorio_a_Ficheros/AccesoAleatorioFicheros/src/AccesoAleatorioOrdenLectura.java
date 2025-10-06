import java.io.*;

public class AccesoAleatorioOrdenLectura {
    public static void main(String[] args) {
        try {
            // Escribir registros
            RandomAccessFile raf = new RandomAccessFile("registros.dat", "rw");
            raf.writeUTF("Registro 1");
            raf.writeUTF("Registro 2");
            raf.writeUTF("Registro 3");
            raf.seek(0);

            // Guardo en variables las posiciones de los registros
            long pos1 = raf.getFilePointer();
            raf.readUTF(); // Lee Registro 1
            long pos2 = raf.getFilePointer();
            raf.readUTF(); // Lee Registro 2
            long pos3 = raf.getFilePointer();
            raf.readUTF(); // Lee Registro 3
            raf.seek(0); // Volver al inicio

            // Leo los registros en diferente orden:

            // 1. Voy a la posición del 3er registro
            raf.seek(pos3);
            // Leo y muestro el 3er registro
            String reg3 = raf.readUTF();
            System.out.println("Registro 3: " + reg3);

            // 2. Voy a la posición del 1er registro
            raf.seek(pos1);
            // Leo y muestro el primer registro
            String reg1 = raf.readUTF();
            System.out.println("Registro 1: " + reg1);

            // 3. Voy a la posición del 2º registro
            raf.seek(pos2);
            // Leo y muestro el segundo registro
            String reg2 = raf.readUTF();
            System.out.println("Registro 2: " + reg2);

            raf.close(); // Cierro el puntero
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}