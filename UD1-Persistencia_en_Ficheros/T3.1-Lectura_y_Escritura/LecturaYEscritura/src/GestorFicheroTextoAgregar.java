import java.io.*;

public class GestorFicheroTextoAgregar {
    public static void main(String[] args) {
        try {
            // Escritura
            FileWriter fw = new FileWriter("datos/registro.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("Registro 1");
            bw.newLine();
            bw.write("Registro 2");
            bw.newLine();
            bw.write("Registro 3");
            bw.newLine();

            bw.flush();
            bw.close();
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