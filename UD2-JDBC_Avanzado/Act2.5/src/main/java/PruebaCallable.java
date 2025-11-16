import conexion.ConexionPool;

import java.sql.*;

public class PruebaCallable {

    public static void main(String[] args) {

        try (Connection con = ConexionPool.getConexion();
             CallableStatement cs = con.prepareCall("{ CALL incrementar_salario(?, ?, ?) }")) {

            cs.setInt(1, 1);
            cs.setDouble(2, 1500); // Suma 1500 € al salario actual

            cs.registerOutParameter(3, java.sql.Types.DOUBLE); // Le digo que el parametro 3 es un double

            cs.execute(); // (Si da error lanzará SQLException)

            double nuevoSalario = cs.getDouble(3); // Obtengo el parámetro de salida (nuevo salario)
            System.out.println("Salario actualizado correctamente\nNuevo salario: " + nuevoSalario + " €");

        } catch (SQLException e) {
            System.out.println("Error al actualizar el salario del empleado.");
            e.printStackTrace();
        }

        ConexionPool.cerrarPool();
    }
}
