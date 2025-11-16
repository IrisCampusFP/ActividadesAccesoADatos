package service;

import config.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.*;

public class ProcedimientosService {

    // Metodo para actualizar los salarios de los empleados en un departamento
    public int actualizarSalariosDepartamento(String departamento, double porcentaje) {
        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cstmt = conn.prepareCall("{call actualizar_salario_departamento(?, ?, ?)}")) {

            // Parámetros de entrada (IN)
            cstmt.setString(1, departamento);
            cstmt.setBigDecimal(2, BigDecimal.valueOf(porcentaje));

            // Parámetro de salida (OUT)
            cstmt.registerOutParameter(3, Types.INTEGER);

            // Ejecución del procedimiento almacenado
            cstmt.execute();

            // Obtiene el número de empleados actualizados
            int empleadosActualizados = cstmt.getInt(3);

            return empleadosActualizados;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Retorna -1 si ocurre algún error
        }
    }

    // Metodo para actualizar el estado de un empleado (activo/inactivo)
    public boolean actualizarEstadoEmpleado(int idEmpleado, boolean estado) {
        try (Connection conn = DatabaseConfig.getConnection();
             CallableStatement cstmt = conn.prepareCall("{call actualizar_estado_empleado(?, ?)}")) {

            // Parámetros de entrada
            cstmt.setInt(1, idEmpleado);
            cstmt.setBoolean(2, estado);

            // Ejecución del procedimiento
            cstmt.execute();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
