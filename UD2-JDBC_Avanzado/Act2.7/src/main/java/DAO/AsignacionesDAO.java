package DAO;

import conexion.ConexionPool;

import java.sql.*;

public class AsignacionesDAO {

    // Metodo que ejecuta el procedimiento almacenado para asignar empleados a proyectos
    public static void asignarEmpleadoAProyecto(int idEmpleado, int idProyecto, String rol) {

        try (Connection con = ConexionPool.getConexion();
             CallableStatement cs = con.prepareCall("{ CALL asignar_empleado_a_proyecto(?, ?, ?) }")) {

            cs.setInt(1, idEmpleado);
            cs.setInt(2, idProyecto);
            cs.setString(3, rol);

            cs.execute();

            System.out.println("Empleado " + idEmpleado + " asignado al proyecto " + idProyecto + " correctamente");

        } catch (SQLException e) {
            System.out.println("Error al asignar el empleado " + idEmpleado + " al proyecto " + idProyecto);
            System.out.println("Mensaje SQL: " + e.getMessage());
        }
    }

    // Metodo para mostrar todos los registros de la tabla asignaciones
    public static void mostrarAsignaciones() {
        String sentenciaSelect = """ 
                SELECT a.id, e.nombre AS empleado, p.nombre AS proyecto, a.rol, a.fecha_asignacion
                FROM asignaciones a
                JOIN empleados e ON a.id_empleado = e.id
                JOIN proyectos p ON a.id_proyecto = p.id
                ORDER BY a.id
                """;

        try (Connection con = ConexionPool.getConexion();
             PreparedStatement ps = con.prepareStatement(sentenciaSelect);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n··· ASIGNACIONES ···");
            while (rs.next()) {
                System.out.printf(
                        "ID: %d | Empleado: %s | Proyecto: %s | Rol: %s | Fecha: %s%n",
                        rs.getInt("id"),
                        rs.getString("empleado"),
                        rs.getString("proyecto"),
                        rs.getString("rol"),
                        rs.getDate("fecha_asignacion")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener las asignaciones");
            e.printStackTrace();
        }
    }
}
