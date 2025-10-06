import java.io.File;

public class RutaRelativaExtra {
    // Método de log personalizado
    public static void log(String mensaje) {
        System.out.println("[LOG] " + mensaje);
    }

    public static void main(String[] args) {
        // Obtener ruta base del proyecto
        String rutaBase = System.getProperty("user.dir");
        String separador = File.separator;
        // Construir ruta completa relativa
        String rutaRelativa = rutaBase + separador + "datos" + separador + "ejemplo.txt";
        // Crear objeto File con esa ruta
        File archivo = new File(rutaRelativa);

        // Mostrar información usando el log personalizado
        log("Ruta base del proyecto: " + rutaBase);
        log("Separador de carpetas del sistema: " + separador);
        log("Ruta relativa completa: " + rutaRelativa);
        log("¿Existe el archivo? " + archivo.exists());
        log("Ruta absoluta real: " + archivo.getAbsolutePath());
        log("Nombre del archivo: " + archivo.getName());
        log("Carpeta donde está contenido: " + archivo.getParent());
    }
}