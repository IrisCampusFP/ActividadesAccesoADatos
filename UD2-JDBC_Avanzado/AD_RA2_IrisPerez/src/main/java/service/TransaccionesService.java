package service;

import config.DatabaseConfig;
import dao.GeneroDAO;
import dao.LibroDAO;
import model.Genero;
import model.Libro;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class TransaccionesService {
    private GeneroDAO generoDAO = new GeneroDAO();
    private LibroDAO libroDAO = new LibroDAO();

    // Inserción de un género una lista de libros de dicho género
    public boolean insertarGeneroYLibros(Genero genero, ArrayList<Libro> libros) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // PASO 1: INSERTAR EL GENERO
            generoDAO.crear(genero);

            // PASO 2: INSERTO LOS LIBROS DE LA LISTA UNO A UNO
            for (Libro libro : libros) {
                libroDAO.crear(libro);
            }

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
}
