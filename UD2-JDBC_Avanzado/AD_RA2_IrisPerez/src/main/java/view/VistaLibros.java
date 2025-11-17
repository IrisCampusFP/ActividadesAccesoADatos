package view;

import dao.GeneroDAO;
import model.Genero;
import model.Libro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static utils.Utils.*;

public class VistaLibros {
    private GeneroDAO generoDAO = new GeneroDAO();

    // Metodo para solicitar datos de un nuevo libro
    public Libro pedirDatosLibro() {
        System.out.println("INTRODUCE LOS DATOS DEL LIBRO");

        System.out.print("Título del libro: ");
        String titulo = sc.nextLine();

        System.out.print("Autor: ");
        String autor = leerString();

        System.out.print("Precio: ");
        double precio = pedirPrecio();

        System.out.print("Stock: ");
        int stock = leerEntero();

        System.out.print("Id de género: ");
        int idGenero  = pedirGenero();

        Genero genero = generoDAO.obtenerPorId(idGenero);
        return new Libro(0, titulo, autor, precio, stock, genero);
    }

    // Metodo para asegurar que el precio no sea negativo
    public double pedirPrecio() {
        double precio;
        do {
            precio = leerDouble();
            if (precio < 0) {
                System.out.println("El precio no puede ser negativo.");
                System.out.print("Por favor, vuelve a introducir el precio: ");
            }
        } while (precio < 0);
        return precio;
    }

    // Solicita un género y comprueba si existe en la base de datos
    public int pedirGenero() {
        int idGenero = leerEntero();

        // Comprobar si el departamento existe en la base de datos
        if (!generoDAO.existeGenero(idGenero)) {
            System.out.println("No se ha encontrado ningún género con id '" + idGenero + "' en la base de datos.");
            System.out.println("Vuelve a introducir un id de género.");
            return pedirGenero();
        }

        return idGenero;
    }

    // Metodo para solicitar un ID de libro
    public int solicitarIdLibro() {
        System.out.print("Introduce el ID del libro: ");
        return leerEntero();
    }

    // Metodo para mostrar los datos de un libro
    public void mostrarDatosLibro(Optional<Libro> l) {
        if (l.isPresent()) {
            Libro libro = l.get();
            System.out.println(libro);
        } else {
            System.out.println("Error al mostrar los datos del libro.");
        }
    }

    // Metodo para mostrar la lista de libros
    public void mostrarLibros(List<Libro> libros) {
        for (Libro p : libros) {
            mostrarDatosLibro(Optional.of(p));
        }
    }

    // Metodo para solicitar la confirmación antes de eliminar un libro
    public boolean solicitarConfirmacion() {
        System.out.println("\n¿Estás segur@ de que quieres eliminar este libro?");
        return preguntaSiNo();
    }

    // Metodo para mostrar el menú de campos a modificar
    public void menuCampoAModificar() {
        System.out.println("\n¿Qué campo quieres modificar?");
        System.out.println("1. Título");
        System.out.println("2. Autor");
        System.out.println("3. Precio");
        System.out.println("4. Stock");
        System.out.println("5. Id de género");
    }

    // Métodos para actualizar los campos del libro

    public String actualizarTitulo() {
        System.out.print("Nuevo título: ");
        return sc.nextLine();
    }

    public String actualizarAutor() {
        System.out.print("Nuevo autor: ");
        return leerString();
    }

    public double actualizarPrecio() {
        System.out.print("Nuevo precio: ");
        return pedirPrecio();
    }

    public int actualizarStock() {
        System.out.print("Introduce el stock: ");
        return leerEntero();
    }

    public int actualizarIdGenero() {
        System.out.print("Introduce el id de género: ");
        return pedirGenero();
    }
}
