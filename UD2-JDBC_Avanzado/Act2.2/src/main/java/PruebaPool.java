import conexion.ConexionPool;

import java.sql.*;

/**
 * Obtención de varias conexiones desde un pool
 * (HikariCP, representado por la clase ConexionPool)
 * Liberación de recursos del pool al finalizar la aplicación.
 *
 * - Uso de try-with-resources para gestionar la conexión.
 * - Manejo básico de SQLException.
 * - Cierre explícito del pool al terminar el programa.
 */
public class PruebaPool {

    public static void main(String[] args) {
        /*
         * try-with-resources asegura que la referencia Connection 'con' se cierre
         * automáticamente cuando se salga del bloque try, incluso si se lanza una excepción.
         * En este ejemplo la conexión proviene de un pool (HikariCP), por lo que cerrar
         * la Connection devuelve la instancia al pool en lugar de cerrarla físicamente.
         */

        // Obtención de 3 conexiones distintas del pool
        for (int i = 1; i <= 3; i++) {
            try (Connection con = ConexionPool.getConexion();
                 // Para cada conexión, ejecuto una consulta select para obtener los nombres de los empleados
                 PreparedStatement ps = con.prepareStatement("SELECT nombre FROM empleados");
                 ResultSet rs = ps.executeQuery()) {

                // Confirmación sencilla de que la conexión fue obtenida correctamente.
                System.out.println("\n···············································");
                System.out.println("· Conexión " + i + " obtenida del pool correctamente. ·");
                System.out.println("···············································");

                // Imprimo los nombres de los empleados
                System.out.println("Nombres de los empleados:");
                while (rs.next()) System.out.println(rs.getString(1));

            } catch (SQLException e) {
                /*
                 * Captura específica de SQLException: en un contexto real se podría
                 * registrar el error en un logger y tomar decisiones según el código SQLState.
                 * Aquí, por simplicidad didáctica, se imprime la traza de la excepción.
                 */
                e.printStackTrace();
            }
        }
        /*
         * El pool de conexiones (DataSource gestionado por HikariCP) debe cerrarse
         * explícitamente cuando la aplicación ya no lo necesita. Llamar a
         * ConexionPool.cerrarPool() garantiza la liberación ordenada de
         * recursos del pool (hilos, conexiones físicas, etc.).
         *
         * (En aplicaciones long-running (servidores) el cierre del
         * pool suele gestionarse en el shutdown hook o en el lifecycle del contenedor.)
         */
        ConexionPool.cerrarPool();
    }
}