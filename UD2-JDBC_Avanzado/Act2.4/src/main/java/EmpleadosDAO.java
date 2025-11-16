import conexion.ConexionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class EmpleadosDAO {

    // Listar todos los empleados
    public void listarEmpleados() {

        try (Connection con = ConexionPool.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM empleados");
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {
                System.out.println("No hay empleados registrados.");
            } else {
                System.out.println("··· LISTA DE EMPLEADOS ···");
                do {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    double salario = rs.getDouble("salario");

                    System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €\n",
                            id, nombre, salario);
                } while (rs.next());
            }

            // Uso ResultSetMetaData para imprimir
            //  - Número de columnas
            //  - Nombre y tipo de cada columna
            ResultSetMetaData meta = rs.getMetaData();
            int columnas = meta.getColumnCount();

            System.out.println("\nNúmero de columnas: " + columnas);

            System.out.println("\nNombre y tipo de cada columna:");
            for (int i = 1; i <= columnas; i++) {
                String nombreCol = meta.getColumnName(i);
                String tipoCol = meta.getColumnTypeName(i);
                System.out.println("Columna " + i + ": " + nombreCol + " | Tipo: " + tipoCol);
            }

            // Uso DatabaseMetaData para imprimir
            //  - Versión del motor
            //  - Nombre del driver JDBC
            DatabaseMetaData dbMeta = con.getMetaData();

            System.out.println("\nBase de datos: " + dbMeta.getDatabaseProductName());
            System.out.println("Versión del motor: " + dbMeta.getDatabaseProductVersion());
            System.out.println("Driver JDBC: " + dbMeta.getDriverName());


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
