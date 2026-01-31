package Actividad4;

import Actividad2.ConexionXMLDB;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultasBiblioteca {

    private static final String COLECCION = "/db/biblioteca";

    private ResourceSet ejecutarXQuery(String xquery)
            throws Exception {

        Collection col = ConexionXMLDB.conectar(COLECCION);
        try {
            XPathQueryService service = (XPathQueryService) col.getService(
                    "XPathQueryService", "1.0");
            return service.query(xquery);
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }

    private List<String> resultadoALista(ResourceSet rs)
            throws Exception {

        List<String> resultados = new ArrayList<>();
        ResourceIterator it = rs.getIterator();
        while (it.hasMoreResources()) {
            resultados.add(
                    (String) it.nextResource().getContent());
        }
        return resultados;
    }

    // 1. Buscar por titulo
    public List<String> buscarPorTitulo(String termino)
            throws Exception {

        String xquery = String.format("""
            for $libro in doc('/db/biblioteca/biblioteca.xml')//libro
            where contains(lower-case($libro/titulo), '%s')
            return concat($libro/titulo, ' - ', $libro/autor)
            """, termino.toLowerCase());

        return resultadoALista(ejecutarXQuery(xquery));
    }

    // 2. Buscar por genero
    public List<String> buscarPorGenero(String genero)
            throws Exception {

        String xquery = String.format("""
            for $libro in doc('/db/biblioteca/biblioteca.xml')//libro[generos/genero='%s']
            order by $libro/titulo
            return $libro/titulo/text()
            """, genero);

        return resultadoALista(ejecutarXQuery(xquery));
    }

    // 3. Estadisticas
    public String obtenerEstadisticas() throws Exception {
        String xquery = """
            let $total := count(doc('/db/biblioteca/biblioteca.xml')//libro)
            let $disponibles := count(doc('/db/biblioteca/biblioteca.xml')//libro[disponible='true'])
            let $promedio := round(avg(doc('/db/biblioteca/biblioteca.xml')//libro/paginas))
            return concat('Total: ', $total, ', Disponibles: ', $disponibles, ', Promedio paginas: ', $promedio)
            """;

        List<String> res = resultadoALista(ejecutarXQuery(xquery));
        return res.isEmpty() ? "" : res.get(0);
    }

    // 4. Libros disponibles
    public List<String> librosDisponibles() throws Exception {
        String xquery = """
            for $libro in doc('/db/biblioteca/biblioteca.xml')//libro[disponible='true']
            order by $libro/titulo
            return concat($libro/titulo, ' - ', $libro/autor)
            """;

        return resultadoALista(ejecutarXQuery(xquery));
    }

    public static void main(String[] args) throws Exception {
        ConsultasBiblioteca cb = new ConsultasBiblioteca();

        System.out.println("=== Buscar 'de' ===");
        cb.buscarPorTitulo("de").forEach(System.out::println);

        System.out.println("\n=== Genero Novela ===");
        cb.buscarPorGenero("Novela").forEach(System.out::println);

        System.out.println("\n=== Estadisticas ===");
        System.out.println(cb.obtenerEstadisticas());

        System.out.println("\n=== Disponibles ===");
        cb.librosDisponibles().forEach(System.out::println);
    }
}