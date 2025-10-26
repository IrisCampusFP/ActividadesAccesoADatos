package nivel2Intermedio_bibliotecaLibros.exportadores;

import nivel2Intermedio_bibliotecaLibros.modelo.Libro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static utils.Utils.*;

public class ExportadorLibrosCSV {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    private static final String SEPARADOR = ";"; // Separador CSV

    public static boolean exportar(ArrayList<Libro> libros, String nombreArchivo) {

        // VALIDACIONES

        if (libros == null || libros.isEmpty()) {
            System.out.println("ERROR: No hay elementos para exportar.");
            return false;
        }

        String rutaCompleta = CARPETA + File.separator + nombreArchivo + "_" + fecha + ".csv";

        if (rutaCompleta == null || rutaCompleta.trim().isEmpty()) {
            System.out.println("ERROR: El nombre del archivo no puede estar vacío.");
            return false;
        }

        // CREAR DIRECTORIO

        if (crearDirectorio(CARPETA)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaCompleta))) {

                // 1. ESCRIBIR ENCABEZADO
                escribirEncabezado(writer);

                // 2. AGRUPAR POR CATEGORÍA
                HashMap<String, ArrayList<Libro>> categorias = agruparPorCategoria(libros);

                // 3. ESCRIBIR CADA CATEGORÍA
                for (String categoria : categorias.keySet()) {
                    ArrayList<Libro> librosCategoria = categorias.get(categoria);

                    escribirSeparadorCategoria(writer, categoria);
                    escribirEncabezadoLibros(writer);

                    for (Libro libro : librosCategoria) {
                        escribirLibro(writer, libro);
                    }

                    escribirResumenCategoria(writer, categoria, librosCategoria);

                    writer.newLine(); // Línea en blanco entre categorías
                }

                return true;

            } catch (IOException e) {
                System.out.println("Error al escribir el archivo CSV: " + e.getMessage());
            }
        }
        return false;
    }

    // Encabezado global del catálogo
    private static void escribirEncabezado(BufferedWriter writer) throws IOException {
        writer.write("# BIBLIOTECA MUNICIPAL - CATÁLOGO DE LIBROS");
        writer.newLine();
        writer.newLine();
    }

    // Agrupa los libros por categoría usando HashMap
    private static HashMap<String, ArrayList<Libro>> agruparPorCategoria(ArrayList<Libro> libros) {
        HashMap<String, ArrayList<Libro>> categorias = new HashMap<>();
        for (Libro l : libros) {
            String cat = l.getCategoria();
            if (!categorias.containsKey(cat)) {
                categorias.put(cat, new ArrayList<>());
            }
            categorias.get(cat).add(l);
        }
        return categorias;
    }

    // Línea separadora de categoría
    private static void escribirSeparadorCategoria(BufferedWriter writer, String categoria) throws IOException {
        writer.write("# CATEGORÍA: " + categoria);
        writer.newLine();
        writer.newLine();
    }

    // Encabezado (nombres de las columnas de libros)
    private static void escribirEncabezadoLibros(BufferedWriter writer) throws IOException {
        writer.write("ISBN" + SEPARADOR);
        writer.write("Título" + SEPARADOR);
        writer.write("Autor" + SEPARADOR);
        writer.write("Año" + SEPARADOR);
        writer.write("Páginas" + SEPARADOR);
        writer.write("Disponible" + SEPARADOR);
        writer.write("Préstamos");
        writer.newLine();
    }

    // Escribe todos los campos de cada libro
    private static void escribirLibro(BufferedWriter writer, Libro l) throws IOException {
        writer.write(escaparCSV(l.getIsbn()) + SEPARADOR);
        writer.write(escaparCSV(l.getTitulo()) + SEPARADOR);
        writer.write(escaparCSV(l.getAutor()) + SEPARADOR);
        writer.write(l.getAñoPublicacion() + SEPARADOR);
        writer.write(l.getNumPaginas() + SEPARADOR);
        writer.write(l.getDisponible() + SEPARADOR);
        writer.write(l.getPrestamos()); // El último elemento SIN SEPARADOR
        writer.newLine();
    }

    // Escribe el resumen de la categoría
    private static void escribirResumenCategoria(BufferedWriter writer, String categoria, ArrayList<Libro> libros) throws IOException {
        int totalLibros = libros.size();
        int totalPrestamos = 0;
        for (Libro l : libros) {
            totalPrestamos += l.getPrestamos();
        }

        writer.write("# Subtotal " + categoria + ": " + totalLibros + " libros, " + totalPrestamos + " préstamos");
        writer.newLine();
    }
}