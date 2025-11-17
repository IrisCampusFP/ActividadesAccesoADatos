package dao;

import config.DatabaseConfig;
import model.Genero;
import model.Libro;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroDAO {
    private GeneroDAO generoDAO = new GeneroDAO();

    // Crear un nuevo libro
    public int crear(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, precio, stock, id_genero) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, libro.getTitulo());
            pst.setString(2, libro.getAutor());
            pst.setDouble(3, libro.getPrecio());
            pst.setInt(4, libro.getStock());
            pst.setInt(5, libro.getGenero().getIdGenero());

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

    // Obtener todos los libros
    public List<Libro> obtenerTodos() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Genero genero = generoDAO.obtenerPorId(rs.getInt("id_genero"));
                Libro libro = new Libro(
                        rs.getInt("id_libro"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        genero
                );
                libros.add(libro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libros;
    }

    // Obtener un libro por su ID
    public Optional<Libro> obtenerPorId(int id) {
        String sql = "SELECT * FROM libros WHERE id_libro = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Genero genero = generoDAO.obtenerPorId(rs.getInt("id_genero"));
                    Libro libro = new Libro(
                            rs.getInt("id_libro"),
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            rs.getDouble("precio"),
                            rs.getInt("stock"),
                            genero
                    );
                    return Optional.of(libro);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty(); // Si no se encuentra el libro
    }

    // Actualizar un libro
    public boolean actualizar(Libro libro) {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, precio = ?, stock = ? WHERE id_genero = ?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, libro.getTitulo());
            pst.setString(2, libro.getAutor());
            pst.setDouble(3, libro.getPrecio());
            pst.setInt(4, libro.getStock());
            pst.setInt(5, libro.getGenero().getIdGenero());

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Eliminar un libro por su ID
    public boolean eliminar(int id) {
        String sql = "DELETE FROM libros WHERE id_libro = ?";
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


    // Metodo para comprobar si un libro existe
    public boolean existeLibro(int idLibro) {
        String sql = "SELECT 1 FROM libros WHERE id_libro = ? LIMIT 1";  // Verifica si existe un libro con el ID proporcionado
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idLibro);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();  // Si se encuentra un libro, devolverá true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Si ocurre un error o el libro no existe, retorna false
    }

    // Metodo para consultar el precio de un libro
    public double consultarPrecio(int idLibro){

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT precio FROM libros WHERE id_libro = ?")) {

            stmt.setInt(1, idLibro);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double precio = rs.getDouble("precio");
                return precio;
            } else {
                throw new SQLException("No se ha encontrado el libro");
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar el presupuesto del libro.");
            e.printStackTrace();
            return -1;
        }
    }

}
