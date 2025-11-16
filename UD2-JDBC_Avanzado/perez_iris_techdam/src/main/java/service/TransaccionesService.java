package service;

import config.DatabaseConfig;
import dao.AsignacionDAO;
import dao.ProyectoDAO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class TransaccionesService {
    private AsignacionDAO asignacionDAO = new AsignacionDAO();
    private ProyectoDAO proyectoDAO = new ProyectoDAO();

    // Opción A: Transferencia de presupuesto entre proyectos
    public boolean transferirPresupuesto(int proyectoOrigenId, int proyectoDestinoId, BigDecimal monto) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Restar de origen
            proyectoDAO.restarPresupuesto(conn, proyectoOrigenId, monto);

            // 2. Sumar a destino
            proyectoDAO.sumarPresupuesto(conn, proyectoDestinoId, monto);

            // Confirmar transacción
            conn.commit();
            return true;
        } catch (SQLException e) {
            // Si ocurre algún error durante la transferencia se revierten los cambios
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir transacción
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            // Cerrar la conexión
            DatabaseConfig.closeConnection(conn);
        }
    }

    // Opción B: Asignación múltiple con Savepoint
    public void asignarEmpleadosConSavepoint(int proyectoId, List<Integer> empleadoIds) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            for (int empId : empleadoIds) {
                Savepoint sp = conn.setSavepoint("SP_" + empId); // Crea un savepoint para cada empleado
                try {
                    // Crear asignación de empleado a proyecto
                    asignacionDAO.crear(conn, empId, proyectoId);
                    System.out.println("Empleado " + empId + " asignado al proyecto " + proyectoId);
                } catch (SQLException e) {
                    // Si hay algún error en la asignación, se revierten los cambios hasta el savepoint
                    conn.rollback(sp);
                    System.out.println("Error al asignar el empleado " + empId + " al proyecto " +
                            proyectoId + ", se ha revertido la asignación.");
                }
            }

            // Confirmar transacción
            conn.commit();
            System.out.println("Asignaciones completadas.");
        } catch (SQLException e) {
            // Si ocurre un error, revertir todos los cambios
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Error en el proceso de asignación múltiple: " + e.getMessage());
        } finally {
            // Cerrar la conexión
            DatabaseConfig.closeConnection(conn);
        }
    }
}
