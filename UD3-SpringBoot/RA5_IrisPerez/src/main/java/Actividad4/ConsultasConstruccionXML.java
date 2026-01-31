package Actividad4;

public class ConsultasConstruccionXML {
    
    public static void main(String[] args) throws Exception {
        String col = "/db/biblioteca";
        
        System.out.println("1. Lista HTML:");
        String q1 = "<ul>" +
                    "{for $libro in doc('/db/biblioteca/biblioteca.xml')//libro " +
                    "return <li>{$libro/titulo/text()}</li>}" +
                    "</ul>";
        System.out.println(ConsultasXQuery.ejecutarXQuery(col, q1));
        
        System.out.println("\n2. XML simplificado:");
        String q2 = "<catalogo>" +
                    "{for $libro in doc('/db/biblioteca/biblioteca.xml')//libro " +
                    "return <item id=\"{$libro/@id}\">" +
                    "<titulo>{$libro/titulo/text()}</titulo>" +
                    "<autor>{$libro/autor/text()}</autor>" +
                    "</item>}" +
                    "</catalogo>";
        System.out.println(ConsultasXQuery.ejecutarXQuery(col, q2));
        
        System.out.println("\n3. Clasificacion clasico/moderno:");
        String q3 = "<catalogo>" +
                    "{for $libro in doc('/db/biblioteca/biblioteca.xml')//libro " +
                    "let $tipo := if ($libro/anio < 1970) then 'clasico' else 'moderno' " +
                    "order by $tipo, $libro/titulo " +
                    "return <libro tipo=\"{$tipo}\" id=\"{$libro/@id}\">" +
                    "{$libro/titulo}{$libro/anio}" +
                    "</libro>}" +
                    "</catalogo>";
        System.out.println(ConsultasXQuery.ejecutarXQuery(col, q3));
    }
}