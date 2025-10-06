import java.io.*;

public class GestorFicheroTextoSoloFW {
    public static void main(String[] args) {
        try {
            // Escritura SOLO CON FILEWRITER (sin BufferedWriter)
            FileWriter fw = new FileWriter("datos/registro.txt");
            fw.write("Registro 1\n");
            fw.write("Registro 2\n");
            fw.write("Registro 3\n");
            fw.close();
            System.out.println("Archivo escrito con éxito.");

            // Lectura
            FileReader fr = new FileReader("datos/registro.txt");
            BufferedReader br = new BufferedReader(fr);
            String linea;
            System.out.println("Contenido del archivo:");
            while ((linea = br.readLine()) != null) {
                System.out.println("> " + linea);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}