package Actividad4;

import Actividad3.AlmacenDocumentos;

public class ConsultasBasicas {
    
    public static void main(String[] args) throws Exception {
        String col = "/db/biblioteca";
        String doc = "biblioteca.xml";
        
        AlmacenDocumentos.guardarDesdeFichero(col, "datos/biblioteca.xml");
        
        System.out.println("1. Todos los títulos:\n" + ConsultasXQuery.ejecutarXPath(col, doc, "//libro/titulo/text()"));
        System.out.println("\n2. Autor del libro L002:\n" + ConsultasXQuery.ejecutarXPath(col, doc, "//libro[@id='L002']/autor/text()"));
        System.out.println("\n3. Libros despues 1950:\n" + ConsultasXQuery.ejecutarXPath(col, doc, "//libro[anio > 1950]/titulo/text()"));
        System.out.println("\n4. Libros con ISBN:\n" + ConsultasXQuery.ejecutarXPath(col, doc, "//libro[@isbn]/titulo/text()"));
        System.out.println("\n5. Total de libros: " + ConsultasXQuery.ejecutarXQuery(col, "count(doc('/db/biblioteca/biblioteca.xml')//libro)"));
        System.out.println("\n6. Libros con mas de 500 paginas:\n" + ConsultasXQuery.ejecutarXPath(col, doc, "//libro[paginas > 500]/titulo/text()"));
    }
}