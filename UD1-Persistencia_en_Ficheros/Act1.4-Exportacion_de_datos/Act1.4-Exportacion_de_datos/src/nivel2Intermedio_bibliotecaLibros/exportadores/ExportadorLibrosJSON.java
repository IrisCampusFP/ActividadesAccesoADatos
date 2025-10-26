package nivel2Intermedio_bibliotecaLibros.exportadores;

import nivel2Intermedio_bibliotecaLibros.modelo.Libro;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static utils.Utils.*;

public class ExportadorLibrosJSON {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    private static final String INDENTACION = "    ";
    private static final String INDENTACION2 = INDENTACION + INDENTACION;
    private static final String INDENTACION3 = INDENTACION2 + INDENTACION;
    private static final String INDENTACION4 = INDENTACION3 + INDENTACION;

    public static boolean exportar(ArrayList<Libro> libros, String nombreArchivo) {
        // VALIDACIONES

        if (libros == null || libros.isEmpty()) {
            System.out.println("ERROR: No hay elementos para exportar.");
            return false;
        }

        String rutaCompleta = CARPETA + File.separator + nombreArchivo + "_" + fecha + ".json";

        if (rutaCompleta == null || rutaCompleta.trim().isEmpty()) {
            System.out.println("ERROR: El nombre del archivo no puede estar vacío.");
            return false;
        }

        // CREAR DIRECTORIO
        if (crearDirectorio(CARPETA)) {
            // Utilizo OutputStreamWriter y FileOutputStream para aplicar UTF_8
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rutaCompleta), StandardCharsets.UTF_8))) {

                // 1. APERTURA DEL OBJETO RAÍZ
                bw.write("{");
                bw.newLine();
                bw.write(INDENTACION + "\"biblioteca\": {");
                bw.newLine();

                // 2. INFORMACIÓN GENERAL
                String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                int totalLibros = libros.size();

                bw.write(INDENTACION2 + "\"informacion\": {");
                bw.newLine();
                bw.write(INDENTACION3 + "\"nombre\": \"Biblioteca Municipal\",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"fecha\": \"" + escaparJSON(fechaActual) + "\",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"totalLibros\": " + totalLibros);
                bw.newLine();
                bw.write(INDENTACION2 + "},");
                bw.newLine();

                // 3. AGRUPAR POR CATEGORÍA
                HashMap<String, ArrayList<Libro>> categorias = agruparPorCategoria(libros);

                bw.write(INDENTACION2 + "\"categorias\": {");
                bw.newLine();

                // --- CÁLCULOS GLOBALES ---
                int totalCategorias = categorias.size();
                int librosDisponibles = 0;
                int librosPrestados = 0;
                int totalPrestamosHistorico = 0;

                // Para controlar la coma entre categorías
                int categoriasEscritas = 0;

                // 4. ESCRIBIR CADA CATEGORÍA
                for (String categoria : categorias.keySet()) {
                    ArrayList<Libro> librosCategoria = categorias.get(categoria);

                    // --- CÁLCULOS POR CATEGORÍA ---
                    int totalLibrosCategoria = librosCategoria.size();
                    int totalPrestamosCategoria = 0;
                    String libroMasPrestado = "";
                    int maxPrestamos = -1;

                    for (Libro l : librosCategoria) {
                        if (l.getDisponible()) {
                            librosDisponibles++;
                        } else {
                            librosPrestados++;
                        }
                        totalPrestamosCategoria += l.getPrestamos();
                        totalPrestamosHistorico += l.getPrestamos();

                        if (l.getPrestamos() > maxPrestamos) {
                            maxPrestamos = l.getPrestamos();
                            libroMasPrestado = l.getTitulo();
                        }
                    }

                    double prestamosMedioCategoria = totalLibrosCategoria > 0 ?
                            (double) totalPrestamosCategoria / totalLibrosCategoria : 0;

                    // ESCRIBIR OBJETO CATEGORÍA
                    bw.write(INDENTACION3 + "\"" + escaparJSON(categoria) + "\": {");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"totalLibros\": " + totalLibrosCategoria + ",");
                    bw.newLine();

                    // LIBROS DE LA CATEGORÍA (ARRAY)
                    bw.write(INDENTACION4 + "\"libros\": [");
                    bw.newLine();
                    for (int i = 0; i < librosCategoria.size(); i++) {
                        Libro l = librosCategoria.get(i);
                        bw.write(INDENTACION4 + INDENTACION + "{");
                        bw.newLine();
                        bw.write(INDENTACION4 + INDENTACION2 + "\"isbn\": \"" + escaparJSON(l.getIsbn()) + "\",");
                        bw.newLine();
                        bw.write(INDENTACION4 + INDENTACION2 + "\"titulo\": \"" + escaparJSON(l.getTitulo()) + "\",");
                        bw.newLine();
                        bw.write(INDENTACION4 + INDENTACION2 + "\"autor\": \"" + escaparJSON(l.getAutor()) + "\",");
                        bw.newLine();
                        bw.write(INDENTACION4 + INDENTACION2 + "\"año\": " + l.getAñoPublicacion() + ",");
                        bw.newLine();
                        bw.write(INDENTACION4 + INDENTACION2 + "\"paginas\": " + l.getNumPaginas() + ",");
                        bw.newLine();
                        bw.write(INDENTACION4 + INDENTACION2 + "\"disponible\": " + l.getDisponible() + ",");
                        bw.newLine();
                        bw.write(INDENTACION4 + INDENTACION2 + "\"prestamos\": " + l.getPrestamos());
                        bw.newLine();
                        bw.write(INDENTACION4 + INDENTACION + "}" + (i < librosCategoria.size() - 1 ? "," : ""));
                        bw.newLine();
                    }
                    bw.write(INDENTACION4 + "],");
                    bw.newLine();

                    // ESTADÍSTICAS DE LA CATEGORÍA
                    bw.write(INDENTACION4 + "\"estadisticas\": {");
                    bw.newLine();
                    bw.write(INDENTACION4 + INDENTACION + "\"totalPrestamos\": " + totalPrestamosCategoria + ",");
                    bw.newLine();
                    bw.write(INDENTACION4 + INDENTACION + "\"prestamosMedio\": " + formatearDouble(prestamosMedioCategoria) + ",");
                    bw.newLine();
                    bw.write(INDENTACION4 + INDENTACION + "\"libroMasPrestado\": \"" + escaparJSON(libroMasPrestado) + "\"");
                    bw.newLine();
                    bw.write(INDENTACION4 + "}");
                    bw.newLine();

                    // CIERRE DE CATEGORÍA
                    categoriasEscritas++;
                    bw.write(INDENTACION3 + "}" + (categoriasEscritas < categorias.size() ? "," : ""));
                    bw.newLine();
                }

                bw.write(INDENTACION2 + "},"); // cierre de "categorias"
                bw.newLine();

                // 5. RESUMEN GLOBAL
                bw.write(INDENTACION2 + "\"resumenGlobal\": {");
                bw.newLine();
                bw.write(INDENTACION3 + "\"totalCategorias\": " + totalCategorias + ",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"totalLibros\": " + totalLibros + ",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"librosDisponibles\": " + librosDisponibles + ",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"librosPrestados\": " + librosPrestados + ",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"totalPrestamosHistorico\": " + totalPrestamosHistorico);
                bw.newLine();
                bw.write(INDENTACION2 + "}");
                bw.newLine();

                bw.write(INDENTACION + "}"); // CIERRE DEL OBJETO BIBLIOTECA
                bw.newLine();
                bw.write("}"); // CIERRE DEL OBJETO RAÍZ

                return true;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false; // (no se ha creado el directorio)
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