package DAO;

import conexion.ConexionPool;

import java.sql.*;

public class EmpleadosDAO {

    // Metodo para insertar empleados
    public static void insertarEmpleado(String nombre, double salario) {

        try (Connection con = ConexionPool.getConexion();
             PreparedStatement ps = con.prepareStatement("INSERT INTO empleados (nombre, salario) VALUES (?, ?)")) {

            ps.setString(1, nombre);
            ps.setDouble(2, salario);
            ps.executeUpdate();

            System.out.println("Empleado insertado correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al insertar empleado");
            e.printStackTrace();
        }
    }

    // Metodo para mostrar todos los empleados
    public static void mostrarEmpleados() {

        try (Connection con = ConexionPool.getConexion();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM empleados");
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n··· EMPLEADOS ···");
            while (rs.next()) {
                System.out.printf("ID: %d | Nombre: %s | Salario: %.2f%n",
                        rs.getInt("id"), rs.getString("nombre"), rs.getDouble("salario")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener los empleados");
            e.printStackTrace();
        }
    }
}