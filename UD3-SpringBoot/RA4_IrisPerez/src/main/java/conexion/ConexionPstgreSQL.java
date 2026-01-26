package conexion;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionPstgreSQL {
    private static final String URL = "jdbc:postgresql://localhost:5433/RA4";
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "root123";
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USUARIO, PASSWORD)) {

            System.out.println("Conexión establecida con PostgreSQL");

            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Servidor: " + meta.getDatabaseProductName());
            System.out.println("Versión: " + meta.getDatabaseProductVersion());

        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
