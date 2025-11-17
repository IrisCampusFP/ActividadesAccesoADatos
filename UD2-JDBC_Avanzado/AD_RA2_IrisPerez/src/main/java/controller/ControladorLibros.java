package controller;

import dao.GeneroDAO;
import dao.LibroDAO;
import model.Genero;
import model.Libro;
import view.Menus;
import view.VistaLibros;

import java.util.List;
import java.util.Optional;

import static utils.Utils.*;

public class ControladorLibros {

    private final Menus m = new Menus();
    private final VistaLibros vista = new VistaLibros();
    private final LibroDAO libroDAO = new LibroDAO();
    //private final TransaccionesService transacciones = new TransaccionesService();
    private final GeneroDAO generoDAO = new GeneroDAO();

    public void menuLibros() {
        while (true) {
            m.menuLibros();
            int opcion = leerOpcion();
            System.out.println();

            switch (opcion) {
                case 1:
                    crearLibro();
                    break;
                case 2:
                    mostrarTodos();
                    break;
                case 3:
                    mostrarLibroPorId();
                    break;
                case 4:
                    actualizarLibro();
                    break;
                case 5:
                    eliminarLibro();
                    break;
                case 0:
                    System.out.println("Volviendo al menú principal...");
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
            pausar();
        }
    }

    public int pedirIdLibro() {
        int idLibro = leerEntero();
        while (!libroDAO.existeLibro(idLibro)) {
            System.out.println("No hay ningún libro registrado con id '" + idLibro + "'.");
            System.out.print("Introduce de nuevo el id del libro: ");
            return pedirIdLibro();
        }
        return idLibro;
    }


    private void crearLibro() {
        Libro l = vista.pedirDatosLibro();
        int idGenerado;
        if ((idGenerado = libroDAO.crear(l)) != -1) {
            System.out.println("Libro creado correctamente. (Id del libro: " + idGenerado + ")" );
        } else {
            System.out.println("No se ha creado el libro.");
        }
    }

    private void mostrarTodos() {
        // Se obtienen todos los libros de la base de datos
        List<Libro> listaLibros = libroDAO.obtenerTodos();
        if (listaLibros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        // Envío los libros a la vista para mostrarlos
        vista.mostrarLibros(listaLibros);
    }

    private void mostrarLibroPorId() {
        int id = vista.solicitarIdLibro();
        Optional libro = libroDAO.obtenerPorId(id);
        if (libro.isEmpty()) {
            System.out.println("Libro no encontrado.");
        } else {
            vista.mostrarDatosLibro(libro);
        }
    }

    private void actualizarLibro() {
        // Se solicita el id del libro a actualizar
        int id = vista.solicitarIdLibro();
        // Se obtiene el libro a actualizar de la base de datos
        Optional<Libro> libro = libroDAO.obtenerPorId(id);

        // Si el libro existe, se piden al usuario los nuevos datos y se actualiza el registro en la base de datos
        if (libro.isPresent()) {
            Libro libroOriginal = libro.get();

            // Se muestran los datos del libro antes de actualizar
            System.out.println("DATOS ACTUALES DEL PROYECTO: ");
            vista.mostrarDatosLibro(libro);

            String titulo = libroOriginal.getTitulo();
            String autor = libroOriginal.getAutor();
            double precio = libroOriginal.getPrecio();
            int stock = libroOriginal.getStock();
            Genero genero = libroOriginal.getGenero();

            int campo;
            do {
                // Se pregunta el campo a modificar
                vista.menuCampoAModificar();
                campo = leerOpcion();

                System.out.println();
                switch (campo) {
                    case 1:
                        titulo = vista.actualizarTitulo();
                        break;
                    case 2:
                        autor = vista.actualizarAutor();
                        break;
                    case 3:
                        precio = vista.actualizarPrecio();
                        break;
                    case 4:
                        stock = vista.actualizarStock();
                        break;
                    case 5:
                        int idGenero = vista.actualizarIdGenero();
                        genero = generoDAO.obtenerPorId(idGenero);
                        break;
                    default:
                        opcionInvalida();
                }
            } while (campo < 1 || campo > 4);

            // Se crea un nuevo objeto libro con los datos actualizados
            Libro libroActualizado = new Libro(id, titulo, autor, precio, stock, genero);

            // Se actualiza el libro en la base de datos
            if (libroDAO.actualizar(libroActualizado)) {
                System.out.println("Libro actualizado correctamente.");
            } else {
                System.out.println("ERROR: No se ha actualizado el libro.");
            }
        } else {
            System.out.println("No existe ningún libro registrado con el id: " + id);
        }
    }

    private void eliminarLibro() {
        int id = vista.solicitarIdLibro();
        Optional libro = libroDAO.obtenerPorId(id);

        if (libro.isEmpty()) {
            System.out.println("Libro no encontrado.");
        } else {
            vista.mostrarDatosLibro(libro);

            boolean confirmacion = vista.solicitarConfirmacion();
            if (confirmacion) {
                if (libroDAO.eliminar(id)) {
                    System.out.println("Libro " + id + " eliminado correctamente.");
                } else {
                    System.out.println("No se ha eliminado el libro.");
                }
            } else {
                System.out.println("Cancelando operación...");
            }
        }
    }
}
