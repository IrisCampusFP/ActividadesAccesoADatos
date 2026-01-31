package Actividad4;

public class ConsultasAvanzadas {
    
    public static void main(String[] args) throws Exception {
        String col = "/db/biblioteca";
        String doc = "biblioteca.xml";
        
        System.out.println("1. Libros del siglo XX (1900-1999):");
        System.out.println(ConsultasXQuery.ejecutarXPath(col, doc, "//libro[anio >= 1900 and anio <= 1999]/titulo/text()"));
        
        System.out.println("\n2. Libros disponibles con mas de 1000 paginas:");
        System.out.println(ConsultasXQuery.ejecutarXPath(col, doc, "//libro[disponible='true' and paginas > 1000]/titulo/text()"));
        
        System.out.println("\n3. Libros cuyo titulo contiene «de»:");
        System.out.println(ConsultasXQuery.ejecutarXPath(col, doc, "//libro[contains(titulo, 'de')]/titulo/text()"));
        
        System.out.println("\n4. El primer y ultimo libro de la lista:");
        System.out.println(ConsultasXQuery.ejecutarXPath(col, doc, "//libro[1]/titulo/text() | //libro[last()]/titulo/text()"));

        System.out.println("\n5. Libros que NO tienen ISBN:");
        System.out.println(ConsultasXQuery.ejecutarXPath(col, doc, "//libro[not(@isbn)]/titulo/text()"));
        
        System.out.println("\n6. Libros del genero «Novela»");
        System.out.println(ConsultasXQuery.ejecutarXPath(col, doc, "//libro[generos/genero='Novela']/titulo/text()"));
    }
}