package Actividad4;

import Actividad2.ConexionXMLDB;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultasColecciones {

    private static final String COLECCION = "/db/biblioteca";

    private ResourceSet ejecutarXQuery(String xquery) throws Exception {
        Collection col = ConexionXMLDB.conectar(COLECCION);
        try {
            XPathQueryService service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            return service.query(xquery);
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }

    private List<String> resultadoALista(ResourceSet rs) throws Exception {
        List<String> resultados = new ArrayList<>();
        ResourceIterator it = rs.getIterator();
        while (it.hasMoreResources()) {
            resultados.add((String) it.nextResource().getContent());
        }
        return resultados;
    }

    public List<String> todosLosLibros() throws Exception {
        String xquery = """
            for $doc in collection('/db/biblioteca')
            for $libro in $doc//libro
            return concat($libro/titulo, ' - ', $libro/autor)
            """;
        return resultadoALista(ejecutarXQuery(xquery));
    }

    public List<String> encontrarDocumentoConLibro(String idLibro) throws Exception {
        String xquery = String.format("""
            for $doc in collection('/db/biblioteca')
            where $doc//libro[@id='%s']
            return base-uri($doc)
            """, idLibro);
        return resultadoALista(ejecutarXQuery(xquery));
    }

    public List<String> fullTextSearch(String termino) throws Exception {
        String xquery = String.format("""
            for $libro in collection('/db/biblioteca')//libro
            where contains(lower-case($libro/titulo), lower-case('%s'))
            return concat($libro/titulo, ' - ', $libro/autor)
            """, termino);
        return resultadoALista(ejecutarXQuery(xquery));
    }

    public List<String> fullTextSearchCampos(String termino1, String termino2) throws Exception {
        String xquery = String.format("""
            for $libro in collection('/db/biblioteca')//libro
            where contains(lower-case($libro/autor), lower-case('%s')) 
               or contains(lower-case($libro/autor), lower-case('%s'))
               or contains(lower-case($libro/titulo), lower-case('%s'))
               or contains(lower-case($libro/titulo), lower-case('%s'))
            return concat($libro/titulo, ' - ', $libro/autor)
            """, termino1, termino2, termino1, termino2);
        return resultadoALista(ejecutarXQuery(xquery));
    }

    public static void main(String[] args) throws Exception {
        ConsultasColecciones cc = new ConsultasColecciones();

        System.out.println("··· 1. Todos los libros de la coleccion ···");
        cc.todosLosLibros().forEach(System.out::println);

        System.out.println("\n··· Encontrar documento que contiene libro L001 ···");
        cc.encontrarDocumentoConLibro("L001").forEach(System.out::println);

        System.out.println("\n··· Full-text search con Lucene (eXist-db) ('quijote') ···");
        cc.fullTextSearch("quijote").forEach(System.out::println);

        System.out.println("\n··· Busqueda full-text en cualquier campo ('garcia' OR 'marquez') ···");
        cc.fullTextSearchCampos("garcia", "marquez").forEach(System.out::println);
    }
}