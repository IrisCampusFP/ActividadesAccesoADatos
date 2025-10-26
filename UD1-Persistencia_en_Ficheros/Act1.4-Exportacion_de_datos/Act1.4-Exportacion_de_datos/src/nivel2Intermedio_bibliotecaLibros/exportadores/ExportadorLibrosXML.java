package nivel2Intermedio_bibliotecaLibros.exportadores;

import nivel2Intermedio_bibliotecaLibros.modelo.Libro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static utils.Utils.*;

public class ExportadorLibrosXML {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    private static final String INDENTACION = "    ";
    private static final String INDENTACION2 = INDENTACION + INDENTACION;
    private static final String INDENTACION3 = INDENTACION2 + INDENTACION;
    private static final String INDENTACION4 = INDENTACION3 + INDENTACION;

    public static boolean exportar(ArrayList<Libro> libros, String nombreArchivo) {
        try {

            // VALIDACIONES

            if (libros == null || libros.isEmpty()) {
                System.out.println("ERROR: No hay elementos para exportar.");
                return false;
            }

            String rutaCompleta = CARPETA + File.separator + nombreArchivo + "_" + fecha + ".xml";

            if (rutaCompleta == null || rutaCompleta.trim().isEmpty()) {
                System.out.println("ERROR: El nombre del archivo no puede estar vacío.");
                return false;
            }

            // CREAR DIRECTORIO

            if (crearDirectorio(CARPETA)) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaCompleta))) {

                    // 1. Declaración XML y elemento raíz
                    bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    bw.newLine();
                    bw.write("<biblioteca>");
                    bw.newLine();


                    // 2. Información general
                    String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    int totalLibros = libros.size();

                    bw.write(INDENTACION + "<informacion>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<nombre>Biblioteca Municipal</nombre>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<fecha>" + escaparXML(fechaActual) + "</fecha>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<totalLibros>" + totalLibros + "</totalLibros>");
                    bw.newLine();
                    bw.write(INDENTACION + "</informacion>");
                    bw.newLine();


                    // 3. Agrupar por categorías
                    HashMap<String, ArrayList<Libro>> categorias = agruparPorCategoria(libros);

                    bw.write(INDENTACION + "<categorias>");
                    bw.newLine();

                    // Para el resumen global
                    int totalCategorias = categorias.size();
                    int librosDisponibles = 0;
                    int librosPrestados = 0;

                    // 4. Escribir cada categoría
                    for (String categoria : categorias.keySet()) {
                        ArrayList<Libro> librosCategoria = categorias.get(categoria);

                        int totalLibrosCategoria = librosCategoria.size();
                        int totalPrestamosCategoria = 0;
                        int disponiblesCategoria = 0;

                        for (Libro l : librosCategoria) {
                            if (l.getDisponible()) {
                                librosDisponibles++;
                                disponiblesCategoria++;
                            } else {
                                librosPrestados++;
                            }
                            totalPrestamosCategoria += l.getPrestamos();
                        }

                        double prestamosMedioCategoria = totalLibrosCategoria > 0 ? (double) totalPrestamosCategoria / totalLibrosCategoria : 0;

                        // Inicio categoría
                        bw.write(INDENTACION2 + "<categoria nombre=\"" + escaparXML(categoria) + "\" totalLibros=\"" + totalLibrosCategoria + "\">");
                        bw.newLine();

                        // Libros de la categoría
                        for (Libro libro : librosCategoria) {
                            bw.write(INDENTACION3 + "<libro isbn=\"" + escaparXML(libro.getIsbn()) + "\" disponible=\"" + libro.getDisponible() + "\">");
                            bw.newLine();
                            bw.write(INDENTACION4 + "<titulo>" + escaparXML(libro.getTitulo()) + "</titulo>");
                            bw.newLine();
                            bw.write(INDENTACION4 + "<autor>" + escaparXML(libro.getAutor()) + "</autor>");
                            bw.newLine();
                            bw.write(INDENTACION4 + "<año>" + libro.getAñoPublicacion() + "</año>");
                            bw.newLine();
                            bw.write(INDENTACION4 + "<paginas>" + libro.getNumPaginas() + "</paginas>");
                            bw.newLine();
                            bw.write(INDENTACION4 + "<prestamos>" + libro.getPrestamos() + "</prestamos>");
                            bw.newLine();
                            bw.write(INDENTACION3 + "</libro>");
                            bw.newLine();
                        }

                        // Estadísticas de la categoría
                        bw.write(INDENTACION3 + "<estadisticas>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<totalPrestamos>" + totalPrestamosCategoria + "</totalPrestamos>");
                        bw.newLine();
                        bw.write(INDENTACION4 + "<prestamosMedio>" + formatearDouble(prestamosMedioCategoria) + "</prestamosMedio>");
                        bw.newLine();
                        bw.write(INDENTACION3 + "</estadisticas>");
                        bw.newLine();

                        // Fin categoría
                        bw.write(INDENTACION2 + "</categoria>");
                        bw.newLine();
                    }

                    bw.write(INDENTACION + "</categorias>");
                    bw.newLine();

                    // 5. Resumen global
                    bw.write(INDENTACION + "<resumenGlobal>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<totalCategorias>" + totalCategorias + "</totalCategorias>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<totalLibros>" + totalLibros + "</totalLibros>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<librosDisponibles>" + librosDisponibles + "</librosDisponibles>");
                    bw.newLine();
                    bw.write(INDENTACION2 + "<librosPrestados>" + librosPrestados + "</librosPrestados>");
                    bw.newLine();
                    bw.write(INDENTACION + "</resumenGlobal>");
                    bw.newLine();

                    // 6. Cerrar raíz
                    bw.write("</biblioteca>");
                    bw.newLine();
                    return true;
                }
            }
            return false; // (no se ha creado el directorio)
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo XML: " + e.getMessage());
            return false;
        }
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
}