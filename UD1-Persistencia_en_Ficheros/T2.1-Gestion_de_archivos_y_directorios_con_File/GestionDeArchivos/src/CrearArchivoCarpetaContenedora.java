import java.io.File;
import java.io.IOException;
public class CrearArchivoCarpetaContenedora {
    public static void main(String[] args) {
        try {
            // Intento crear un archivo en una carpeta contenedora inexistente

            // Ruta del archivo y carpeta contenedora inexistentes
            File archivo = new File("carpetaInexistente/archivo.txt");

            if (!archivo.exists()) {
                archivo.getParentFile().mkdirs();
                archivo.createNewFile();
                System.out.println("Archivo creado correctamente.");
            } else {
                System.out.println("El archivo ya existe.");
            }

            // 3. Mostrar información del archivo
            System.out.println("Nombre: " + archivo.getName());
            System.out.println("Ruta absoluta: " + archivo.getAbsolutePath());
            System.out.println("¿Se puede leer? " + archivo.canRead());
            System.out.println("¿Se puede escribir? " + archivo.canWrite());
            System.out.println("¿Es un archivo? " + archivo.isFile());

        } catch (IOException e) {
            System.out.println("Error de entrada/salida: " +
                    e.getMessage());
        }
    }
}