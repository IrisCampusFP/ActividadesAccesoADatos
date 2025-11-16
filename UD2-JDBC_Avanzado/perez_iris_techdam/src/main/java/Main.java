import config.DatabaseConfig;
import controller.ControladorPrincipal;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ControladorPrincipal cp = new ControladorPrincipal();

        // Se prueba la conexión con la base de datos inicializando el pool de conexiones
        try (Connection con = DatabaseConfig.getConnection()) {
        } catch (SQLException e) {
            System.out.println("Error al establecer conexión con la base de datos.");
            e.printStackTrace();
        }

        cp.menuPrincipal();
    }
}
