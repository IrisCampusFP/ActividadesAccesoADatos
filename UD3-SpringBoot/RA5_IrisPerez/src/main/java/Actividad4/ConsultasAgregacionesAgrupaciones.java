package Actividad4;

public class ConsultasAgregacionesAgrupaciones {
    
    public static void main(String[] args) throws Exception {
        String col = "/db/biblioteca";
        
        System.out.println("1. Estadisticas:");
        String q1 = "<estadisticas>" +
                    "<total>{count(doc('/db/biblioteca/biblioteca.xml')//libro)}</total>" +
                    "<disponibles>{count(doc('/db/biblioteca/biblioteca.xml')//libro[disponible='true'])}</disponibles>" +
                    "<no-disponibles>{count(doc('/db/biblioteca/biblioteca.xml')//libro[disponible='false'])}</no-disponibles>" +
                    "<promedio-paginas>{round(avg(doc('/db/biblioteca/biblioteca.xml')//libro/paginas))}</promedio-paginas>" +
                    "<total-paginas>{sum(doc('/db/biblioteca/biblioteca.xml')//libro/paginas)}</total-paginas>" +
                    "<libro-mas-antiguo>{min(doc('/db/biblioteca/biblioteca.xml')//libro/anio)}</libro-mas-antiguo>" +
                    "<libro-mas-reciente>{max(doc('/db/biblioteca/biblioteca.xml')//libro/anio)}</libro-mas-reciente>" +
                    "</estadisticas>";
        System.out.println(ConsultasXQuery.ejecutarXQuery(col, q1));
        
        System.out.println("\n2. Agrupar por siglo:");
        String q2 = "for $libro in doc('/db/biblioteca/biblioteca.xml')//libro " +
                    "let $siglo := (xs:integer($libro/anio) idiv 100) + 1 " +
                    "group by $siglo " +
                    "order by $siglo " +
                    "return <siglo numero=\"{$siglo}\">" +
                    "<cantidad>{count($libro)}</cantidad>" +
                    "<titulos>{for $l in $libro return <titulo>{$l/titulo/text()}</titulo>}</titulos>" +
                    "</siglo>";
        System.out.println(ConsultasXQuery.ejecutarXQuery(col, q2));
        
        System.out.println("\n3. Contar por genero:");
        String q3 = "let $generos := distinct-values(doc('/db/biblioteca/biblioteca.xml')//libro/generos/genero) " +
                    "return <generos>{" +
                    "for $g in $generos " +
                    "let $cantidad := count(doc('/db/biblioteca/biblioteca.xml')//libro[generos/genero = $g]) " +
                    "order by $cantidad descending " +
                    "return <genero nombre=\"{$g}\" cantidad=\"{$cantidad}\"/>" +
                    "}</generos>";
        System.out.println(ConsultasXQuery.ejecutarXQuery(col, q3));
    }
}