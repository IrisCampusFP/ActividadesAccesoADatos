import conexion.ConexionPool;

import java.sql.*;

public class TransferenciaBancaria {

    public static void main(String[] args) {
        int idCuentaOrigen = 1;
        int idCuentaDestino = 2;
        double cantidadTransferida = 500;

        Savepoint spRetirada = null;

        // Obtiene una conexión desde la clase ConexionPool y la cierra automáticamente
        try (Connection con = ConexionPool.getConexion()) {

            try (// PreparedStatement para retirar dinero: resta saldo a la cuenta indicada
                 PreparedStatement retirar = con.prepareStatement("UPDATE cuentas SET saldo = saldo - ? WHERE id = ?");
                 // PreparedStatement para ingresar dinero: suma saldo a la cuenta indicada
                 PreparedStatement ingresar = con.prepareStatement("UPDATE cuentas SET saldo = saldo + ? WHERE id = ?");
                 // PreparedStatement para insertar logs
                 PreparedStatement insertarLog = con.prepareStatement("INSERT INTO logs (mensaje) VALUES (?)")) {

                // ········· INICIO DE LA TRANSACCIÓN ·········

                // Desactiva el auto-commit para manejar la transacción manualmente
                con.setAutoCommit(false);

                // ······ 1. PRIMERA ACTUALIZACIÓN (RETIRADA) ······

                // Configura el primer parámetro (cantidad) para la primera actualización
                retirar.setDouble(1, cantidadTransferida);
                // Configura el segundo parámetro (id)
                retirar.setInt(2, idCuentaOrigen);
                // Ejecuta la actualización que retira dinero de la cuenta origen
                retirar.executeUpdate();

                // 2. Registro del log de la retirada (primer paso de la transacción)
                insertarLog.setString(1, "Retirados " + cantidadTransferida + " € de la cuenta con id " + idCuentaOrigen);
                insertarLog.executeUpdate();

                // 3. Savepoint tras completar primer paso y hacer su log
                spRetirada = con.setSavepoint("RETIRADA_COMPLETADA");

                // ······ 4. SEGUNDA ACTUALIZACIÓN (INGRESO) ······

                // Configura el primer parámetro (cantidad) para la segunda actualización
                ingresar.setDouble(1, cantidadTransferida);
                // Configura el segundo parámetro (id)
                ingresar.setInt(2, idCuentaDestino);
                // Ejecuta la actualización que ingresa dinero en la cuenta destino
                ingresar.executeUpdate();

                // *** FUERZO UN ERROR DE PRUEBA
                //if (true) throw new SQLException("Error forzado para probar savepoint");

                // 5. Registro del log del ingreso (segundo paso de la transacción)
                insertarLog.setString(1, "Ingresados " + cantidadTransferida + " € en la cuenta " + idCuentaDestino);
                insertarLog.executeUpdate();

                // *** FUERZO UN SEGUNDO ERROR DE PRUEBA
                if (true) throw new SQLException("Segundo error forzado para probar vuelta al savepoint tras el log del segundo paso");

                // (Si hay algún fallo en este paso, se volverá al savepoint desde el catch)

                // ······ 6. COMMIT ······
                // Confirmación la transacción: ambas actualizaciones se hacen permanentes
                con.commit();
                System.out.println("Transacción realizada con éxito.");
            } catch (SQLException e) {
                // Si ocurre cualquier excepción, se imprime la traza
                e.printStackTrace();

                // Si se ha hecho el savepoint
                if (spRetirada != null) {
                    try {
                        // Se intenta volver al savepoint:
                        con.rollback(spRetirada);
                        con.commit(); // Se confirma SOLO el primer paso (se guardan los cambios de la retirada)
                        System.out.println("Fallo en el segundo paso de la transacción (ingreso en cuenta destino)\n" +
                                "Se mantiene el primer paso (retirada cuenta origen).");
                    } catch (SQLException ex) {
                        // Si hay algun error relacionado con el savepoint
                        // 8. Se llama a rollback, lo cual revierte la transacción completa
                        con.rollback();
                        /* (rollback deshace TODAS las operaciones ejecutadas durante la transacción
                         * dejando las cuentas en el mismo estado que antes de iniciarla) */
                        System.out.println("Error al recuperar desde el savepoint.\nRollback total de la transacción.");
                    }
                } else {
                    // Si no se hace el savepoint (el error ocurre al inicio de la transacción)
                    System.out.print("Error al ejecutar la transacción.");
                    con.rollback(); // Se ejecuta un rollback total
                }
            }

        } catch (SQLException e) { // Si ocurre un error al obtener la conexion
            e.printStackTrace();
        }
    }
}