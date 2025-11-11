package conexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/* Clase de utilidad para obtener y cerrar una única instancia de {@link Connection}.
 *
 * - Muestra la obtención de una conexión JDBC usando {@link DriverManager} y un
 * fichero de propiedades situado en el classpath (\`db.properties\`).
 * - Uso de try-with-resources para recursos IO (\`InputStream\`).
 * - Ciclo de vida básico de una Connection en un ejemplo sencillo.
 *
 * Este ejemplo mantiene una única conexión estática para conservar la lógica
 * original. En aplicaciones concurrentes o productivas se recomienda usar un
 * pool de conexiones (p. ej. HikariCP) y evitar compartir una Connection estática.
 */
public class ConexionBD {
    // Conexión compartida por la clase, se inicializa en getConexion().
    private static Connection conexion = null;

    /**
     * Devuelve la conexión a la base de datos.
     *
     * Flujo:
     * 1. Si la conexión es nula o está cerrada, lee las propiedades desde
     * el recurso \`db.properties\` del classpath y crea una nueva Connection
     * mediante DriverManager.getConnection(...).
     * 2. Si la conexión ya existe y está abierta, simplemente la devuelve.
     *
     * Nota: Se lanza \`Exception\` para simplificar el ejemplo y centrarse en la
     * mecánica de obtención de la conexión. En código de producción conviene
     * usar excepciones más específicas y un manejo robusto.
     *
     * @return instancia de {@link Connection}
     * @throws Exception si ocurre algún error al leer la configuración o abrir la conexión
     */
    public static Connection getConexion() throws IOException, SQLException {
        // Comprueba si la conexión necesita ser creada o reestablecida.
        if (conexion == null || conexion.isClosed()) {
            Properties props = new Properties();

            /*
             * Lectura del fichero de configuración desde el classpath.
             * try-with-resources garantiza el cierre del InputStream aunque se produzca
             * una excepción durante la carga de propiedades.
             *
             * Observación: si el recurso no existe, getResourceAsStream(...) devuelve null;
             * en este ejemplo se mantiene la lógica original (no se añade comprobación).
             */
            try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
                props.load(input);
            }

            // Obtención de la Connection usando los valores leídos del fichero de propiedades.
            conexion = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password"));
            System.out.println("Conexión establecida con la BD");
        }
        return conexion;
    }

    /**
     * Cierra la conexión si está abierta.
     *
     * Buenas prácticas:
     * - Proteger el cierre con un bloque try/catch para evitar que excepciones al
     * cerrar la conexión propaguen errores no controlados.
     * - En aplicaciones con pool de conexiones, cerrar la Connection suele devolverla
     * al pool en lugar de cerrarla físicamente.
     */
    public static void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) conexion.close();
            System.out.println("Conexion con la BD cerrada correctamente.");
        } catch (SQLException e) {
            // En este ejemplo docente imprimimos la traza; en producción usar un logger.
            System.out.print("Error al cerrar la conexion: ");
            e.printStackTrace();
        }
    }
}