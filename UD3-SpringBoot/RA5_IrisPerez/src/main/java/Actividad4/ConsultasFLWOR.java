package Actividad4;

public class ConsultasFLWOR {
    
    public static void main(String[] args) throws Exception {
        String col = "/db/biblioteca";
        
        System.out.println("1. Titulo y autor:");
        String q1 = "for $libro in doc('/db/biblioteca/biblioteca.xml')//libro " +
                    "return concat($libro/titulo, ' - ', $libro/autor)";
        System.out.println(ConsultasXQuery.ejecutarXQuery(col, q1));
        
        System.out.println("\n2. Ordenados por ano:");
        String q2 = "for $libro in doc('/db/biblioteca/biblioteca.xml')//libro " +
                    "order by $libro/anio ascending " +
                    "return $libro/titulo/text()";
        System.out.println(ConsultasXQuery.ejecutarXQuery(col, q2));
        
        System.out.println("\n3. Disponibles por paginas:");
        String q3 = "for $libro in doc('/db/biblioteca/biblioteca.xml')//libro " +
                    "where $libro/disponible = 'true' " +
                    "order by xs:integer($libro/paginas) descending " +
                    "return concat($libro/titulo, ' (', $libro/paginas, ' pags)')";
        System.out.println(ConsultasXQuery.ejecutarXQuery(col, q3));
    }
}