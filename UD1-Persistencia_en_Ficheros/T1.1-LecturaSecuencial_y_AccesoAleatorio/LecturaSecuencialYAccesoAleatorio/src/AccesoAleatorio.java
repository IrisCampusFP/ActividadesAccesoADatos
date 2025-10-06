import java.io.*;
public class AccesoAleatorio {
    public static void main(String[] args) {
        try {
            RandomAccessFile raf = new RandomAccessFile("datos.txt", "r");

            // Cambia el valor para probar: 0, 10, 15, 30, etc.
            long posicion = 45;
            raf.seek(posicion); // Mover el puntero al byte 15

            System.out.println("Lectura desde byte " + posicion + ":");
            String linea = raf.readLine(); // Leer desde esa posición
            System.out.println("> " + linea);

            raf.close();
        } catch (IOException e) {
            System.out.println("Error en acceso aleatorio: " + e.getMessage());
        }
    }
}