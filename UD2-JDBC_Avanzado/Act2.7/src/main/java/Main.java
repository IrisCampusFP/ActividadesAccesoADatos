import DAO.AsignacionesDAO;
import conexion.ConexionPool;
import DAO.EmpleadosDAO;
import DAO.ProyectosDAO;

import java.sql.*;

public class Main {

    public static void main(String[] args) {

        // 1. Inserción de empleados y proyectos con PreparedStatement
        EmpleadosDAO.insertarEmpleado("Ana", 2000);
        EmpleadosDAO.insertarEmpleado("Luis", 1800);

        ProyectosDAO.insertarProyecto("Proyecto A", 10000);
        ProyectosDAO.insertarProyecto("Proyecto B", 5000);

        System.out.println();

        // 2. Ejecución del procedimiento almacenado
        // (asigno el empleado 1 al proyecto 1 con rol de desarrollador)
        AsignacionesDAO.asignarEmpleadoAProyecto(1, 1, "Desarrollador");

        System.out.println();

        // 3. Transacción que incrementa el salario de un empleado y
        // descuenta el importe del presupuesto de un proyecto
        int idEmpleadoIncrementoSalario = 1;
        int idProyectoDescuentoPresupuesto = 1;
        double importe = 300;

        try (Connection con = ConexionPool.getConexion()) {

            con.setAutoCommit(false); // Inicio de la transacción

            try (// PreparedStatement para incrementar el salario del empleado
                 PreparedStatement ps1 = con.prepareStatement("UPDATE empleados SET salario = salario + ? WHERE id = ?");
                 // PreparedStatement para descontar el importe del presupuesto de un proyecto
                 PreparedStatement ps2 = con.prepareStatement("UPDATE proyectos SET presupuesto = presupuesto - ? WHERE id = ?")) {

                // ······ TRANSACCIÓN ······

                // 1. Incremento salario empleado
                ps1.setDouble(1, importe);
                ps1.setInt(2, idEmpleadoIncrementoSalario);
                ps1.executeUpdate();

                // 2. Descuento presupuesto proyecto
                ps2.setDouble(1, importe);
                ps2.setInt(2, idProyectoDescuentoPresupuesto);
                ps2.executeUpdate();

                // 3. Confirmación de la transacción
                con.commit();
                System.out.println("Transacción ejecutada con éxito.");

                System.out.println("Se ha incrementado " + importe + " € el salario del empleado " +
                        idEmpleadoIncrementoSalario + " y se ha descontado el importe del presupuesto del proyecto " + idProyectoDescuentoPresupuesto);
            } catch (SQLException e) {
                System.out.println("Error al ejecutar transacción. Revirtiendo cambios...");
                con.rollback(); // Hago un rollback para revertir los cambios hechos durante la transacción
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener la conexión con la base de datos.");
        }

        // 4. Se muestra el estado final de las tablas
        System.out.println("\nEstado final de las tablas:");
        EmpleadosDAO.mostrarEmpleados();
        ProyectosDAO.mostrarProyectos();
        AsignacionesDAO.mostrarAsignaciones();
    }
}
