import conexion.ConexionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProyectosDAO {

    // Metodo para insertar un proyecto
    public boolean insertarProyecto(String nombre, double presupuesto) {
        String sql = "INSERT INTO proyectos (nombre, presupuesto) VALUES (?, ?)";

        try (Connection con = ConexionPool.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setDouble(2, presupuesto);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // Metodo para actualizar el presupuesto de un proyecto
    public boolean actualizarPresupuesto(int idProyecto, double nuevoPresupuesto) {
        String sql = "UPDATE proyectos SET presupuesto = ? WHERE id = ?";

        try (Connection con = ConexionPool.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, nuevoPresupuesto);
            ps.setInt(2, idProyecto);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // Metodo para eliminar un proyecto
    public boolean eliminarProyecto(int idProyecto) {
        String sql = "DELETE FROM proyectos WHERE id = ?";

        try (Connection con = ConexionPool.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProyecto);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    // Listar proyectos
    public void listarProyectos() {
        String sql = "SELECT id, nombre, presupuesto FROM proyectos";

        try (Connection con = ConexionPool.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {
                System.out.println("No hay proyectos registrados.");
            } else {
                System.out.println("··· LISTA DE PROYECTOS ···");
                do {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    double presupuesto = rs.getDouble("presupuesto");

                    System.out.printf("ID: %d | Nombre: %s | Presupuesto: %.2f €\n",
                            id, nombre, presupuesto);
                } while (rs.next());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
