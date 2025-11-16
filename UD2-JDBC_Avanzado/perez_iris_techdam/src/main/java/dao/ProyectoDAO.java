package dao;

import config.DatabaseConfig;
import model.Proyecto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProyectoDAO {

    // Crear un nuevo proyecto
    public int crear(Proyecto proyecto) {
        String sql = "INSERT INTO proyectos (nombre, presupuesto, fecha_inicio, fecha_fin) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, proyecto.getNombre());
            pst.setBigDecimal(2, proyecto.getPresupuesto());
            pst.setDate(3, Date.valueOf(proyecto.getFechaInicio()));
            pst.setDate(4, Date.valueOf(proyecto.getFechaFin()));

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

    // Obtener todos los proyectos
    public List<Proyecto> obtenerTodos() {
        List<Proyecto> proyectos = new ArrayList<>();
        String sql = "SELECT * FROM proyectos";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Proyecto proyecto = new Proyecto(
                    rs.getInt("id_proyecto"),
                    rs.getString("nombre"),
                    rs.getBigDecimal("presupuesto"),
                    rs.getDate("fecha_inicio").toLocalDate(),
                    rs.getDate("fecha_fin").toLocalDate()
                );
                proyectos.add(proyecto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proyectos;
    }

    // Obtener un proyecto por su ID
    public Optional<Proyecto> obtenerPorId(int id) {
        String sql = "SELECT * FROM proyectos WHERE id_proyecto = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Proyecto proyecto = new Proyecto(
                        rs.getInt("id_proyecto"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("presupuesto"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_fin").toLocalDate()
                    );
                    return Optional.of(proyecto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty(); // Si no se encuentra el proyecto
    }

    // Actualizar un proyecto
    public boolean actualizar(Proyecto proyecto) {
        String sql = "UPDATE proyectos SET nombre = ?, presupuesto = ?, fecha_inicio = ?, fecha_fin = ? WHERE id_proyecto = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, proyecto.getNombre());
            pst.setBigDecimal(2, proyecto.getPresupuesto());
            pst.setDate(3, Date.valueOf(proyecto.getFechaInicio()));
            pst.setDate(4, Date.valueOf(proyecto.getFechaFin()));
            pst.setInt(5, proyecto.getIdProyecto());

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Eliminar un proyecto por su ID
    public boolean eliminar(int id) {
        String sql = "DELETE FROM proyectos WHERE id_proyecto = ?";
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


    // Metodo para restar presupuesto de un proyecto
    public boolean restarPresupuesto(Connection conn, int idProyecto, BigDecimal monto) {
        String sql = "UPDATE proyectos SET presupuesto = presupuesto - ? WHERE id_proyecto = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setBigDecimal(1, monto);
            pst.setInt(2, idProyecto);

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;  // Devuelve true si se ha actualizado correctamente
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Si ocurre algún error, devuelve false
    }

    // MEtodo para sumar presupuesto a un proyecto
    public boolean sumarPresupuesto(Connection conn, int idProyecto, BigDecimal monto) {
        String sql = "UPDATE proyectos SET presupuesto = presupuesto + ? WHERE id_proyecto = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setBigDecimal(1, monto);
            pst.setInt(2, idProyecto);

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;  // Devuelve true si se ha actualizado correctamente
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Si ocurre algún error, devuelve false
    }

    // Metodo para comprobar si un proyecto existe
    public boolean existeProyecto(int idProyecto) {
        String sql = "SELECT 1 FROM proyectos WHERE id_proyecto = ? LIMIT 1";  // Verifica si existe un proyecto con el ID proporcionado
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idProyecto);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();  // Si se encuentra un proyecto, devolverá true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Si ocurre un error o el proyecto no existe, retorna false
    }

    // Metodo para consultar el presupuesto de un proyecto
    public BigDecimal consultarPresupuesto(int idProyecto){

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT presupuesto FROM proyectos WHERE id = ?")) {

            stmt.setInt(1, idProyecto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                BigDecimal presupuesto = rs.getBigDecimal("presupuesto");
                return presupuesto;
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar el presupuesto del proyecto.");
            e.printStackTrace();
            return null;
        }
    }

}
