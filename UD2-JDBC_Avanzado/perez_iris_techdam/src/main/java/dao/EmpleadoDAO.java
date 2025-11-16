package dao;

import config.DatabaseConfig;
import model.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpleadoDAO {

    // Crear un nuevo empleado
    public int crear(Empleado empleado) {
        String sql = "INSERT INTO empleados (nombre, departamento, salario, activo) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, empleado.getNombre());
            pst.setString(2, empleado.getDepartamento());
            pst.setBigDecimal(3, empleado.getSalario());
            pst.setBoolean(4, empleado.isActivo());

            int filasAfectadas = pst.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Devuelve el ID generado
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // En caso de error, devuelve -1
    }

    // Obtener todos los empleados
    public List<Empleado> obtenerTodos() {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT * FROM empleados";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Empleado empleado = new Empleado(
                    rs.getInt("id_empleado"),
                    rs.getString("nombre"),
                    rs.getString("departamento"),
                    rs.getBigDecimal("salario"),
                    rs.getBoolean("activo")
                );
                empleados.add(empleado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empleados;
    }

    // Obtener un empleado por su ID
    public Optional<Empleado> obtenerPorId(int id) {
        String sql = "SELECT * FROM empleados WHERE id_empleado = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Empleado empleado = new Empleado(
                        rs.getInt("id_empleado"),
                        rs.getString("nombre"),
                        rs.getString("departamento"),
                        rs.getBigDecimal("salario"),
                        rs.getBoolean("activo")
                    );
                    return Optional.of(empleado);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty(); // Si no se encuentra el empleado
    }

    // Actualizar un empleado
    public boolean actualizar(Empleado empleado) {
        String sql = "UPDATE empleados SET nombre = ?, departamento = ?, salario = ?, activo = ? WHERE id_empleado = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, empleado.getNombre());
            pst.setString(2, empleado.getDepartamento());
            pst.setBigDecimal(3, empleado.getSalario());
            pst.setBoolean(4, empleado.isActivo());
            pst.setInt(5, empleado.getIdEmpleado());

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Eliminar un empleado por su ID
    public boolean eliminar(int id) {
        String sql = "DELETE FROM empleados WHERE id_empleado = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Metodo optimizado para comprobar si el departamento existe
    public static boolean existeDepartamento(String departamento) {
        String sql = "SELECT 1 FROM empleados WHERE departamento = ? LIMIT 1";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, departamento);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next(); // Si encuentra un registro, significa que el departamento existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Si ocurre un error o no existe el departamento, retorna false
    }

}
