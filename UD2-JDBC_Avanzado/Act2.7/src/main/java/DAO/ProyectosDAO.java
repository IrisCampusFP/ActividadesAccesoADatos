package DAO;

import conexion.ConexionPool;

import java.sql.*;

public class ProyectosDAO {

    // Metodo para insertar proyectos
    public static void insertarProyecto(String nombre, double presupuesto) {

        try (Connection con = ConexionPool.getConexion();
             PreparedStatement ps = con.prepareStatement("INSERT INTO proyectos (nombre, presupuesto) VALUES (?, ?)")) {

            ps.setString(1, nombre);
            ps.setDouble(2, presupuesto);
            ps.executeUpdate();

            System.out.println("Proyecto insertado correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al insertar proyecto");
            e.printStackTrace();
        }
    }

    // Metodo para mostrar todos los proyectos
    public static void mostrarProyectos() {

        try (Connection con = ConexionPool.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM proyectos ORDER BY id");
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n··· PROYECTOS ···");
            while (rs.next()) {
                System.out.printf("ID: %d | Nombre: %s | Presupuesto: %.2f%n",
                        rs.getInt("id"), rs.getString("nombre"), rs.getDouble("presupuesto")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener los proyectos");
            e.printStackTrace();
        }
    }
}
