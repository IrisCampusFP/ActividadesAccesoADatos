package dao;


import config.DatabaseConfig;
import model.Asignacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsignacionDAO {
    // Metodo para asignar un empleado a un proyecto
    public void crear(Connection conn, int empId, int proyectoId) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement("INSERT INTO asignaciones (id_empleado, id_proyecto) VALUES (?, ?)")) {
            pst.setInt(1, empId);
            pst.setInt(2, proyectoId);

            // Ejecución de la consulta
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error al asignar el empleado al proyecto: " + e.getMessage(), e);
        }
    }

    // Metodo para obtener todas las asignaciones
    public List<Asignacion> obtenerAsignaciones() {
        List<Asignacion> listaAsignaciones = new ArrayList<>();

        String sql = """ 
                SELECT * FROM asignaciones a 
                JOIN empleados e ON a.id_empleado = e.id_empleado 
                JOIN proyectos p ON a.id_proyecto = p.id_proyecto  
                ORDER BY a.id_proyecto, e.nombre
                """;

        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Asignacion asignacion = new Asignacion(
                        rs.getInt("id_asignacion"),
                        rs.getInt("id_empleado"),
                        rs.getInt("id_proyecto"),
                        rs.getDate("fecha").toLocalDate()
                );
                listaAsignaciones.add(asignacion);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaAsignaciones;
    }
}
