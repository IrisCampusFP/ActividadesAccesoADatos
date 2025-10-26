package nivel2Intermedio_bibliotecaLibros.modelo;

public class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private String categoria;
    private int añoPublicacion;
    private int numPaginas;
    private boolean disponible;
    private int prestamos;

    // Constructor
    public Libro(String isbn, String titulo, String autor, String categoria, int añoPublicacion, int numPaginas, boolean disponible, int prestamos) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.añoPublicacion = añoPublicacion;
        this.numPaginas = numPaginas;
        this.disponible = disponible;
        this.prestamos = prestamos;
    }

    // Getters
    public String getIsbn() {
        return isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getAñoPublicacion() {
        return añoPublicacion;
    }

    public int getNumPaginas() {
        return numPaginas;
    }

    public boolean getDisponible() {
        return disponible;
    }

    public int getPrestamos() {
        return prestamos;
    }
}