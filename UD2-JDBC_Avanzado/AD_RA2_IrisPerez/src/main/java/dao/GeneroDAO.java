package dao;

import config.DatabaseConfig;
import model.Genero;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GeneroDAO {
    
    // Crear un nuevo genero
    public int crear(Genero genero) {
        String sql = "INSERT INTO generos (descripcion) VALUES (?)";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, genero.getDescripcion());
            
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

    // Obtener todos los generos
    public List<Genero> obtenerTodos() {
        List<Genero> generos = new ArrayList<>();
        String sql = "SELECT * FROM generos";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Genero genero = new Genero(
                        rs.getInt("id_genero"),
                        rs.getString("descripcion")
                );
                generos.add(genero);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generos;
    }

    // Obtener un genero por su ID
    public Genero obtenerPorId(int id) {
        String sql = "SELECT * FROM generos WHERE id_genero = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Genero genero = new Genero(
                            rs.getInt("id_genero"),
                            rs.getString("descripcion")
                            );
                    return genero;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si no se encuentra el genero
    }

    // Actualizar un genero
    public boolean actualizar(Genero genero) {
        String sql = "UPDATE generos SET descripcion = ? WHERE id_genero = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, genero.getDescripcion());
            pst.setInt(2, genero.getIdGenero());

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Eliminar un genero por su ID
    public boolean eliminar(int id) {
        String sql = "DELETE FROM generos WHERE id_genero = ?";
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

    // Metodo para comprobar si un genero existe
    public boolean existeGenero(int idGenero) {
        String sql = "SELECT 1 FROM generos WHERE id_genero = ? LIMIT 1";  // Verifica si existe un genero con el ID proporcionado
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idGenero);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();  // Si se encuentra un genero, devolverá true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Si ocurre un error o el genero no existe, retorna false
    }

    // Metodo para consultar el presupuesto de un genero
    public BigDecimal consultarPresupuesto(int idGenero){

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT presupuesto FROM generos WHERE id_genero = ?")) {

            stmt.setInt(1, idGenero);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                BigDecimal presupuesto = rs.getBigDecimal("presupuesto");
                return presupuesto;
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar el presupuesto del genero.");
            e.printStackTrace();
            return null;
        }
    }
}
