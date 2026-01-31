package Actividad4;

import Actividad2.ConexionXMLDB;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;

public class ConsultasXQuery {

    public static String ejecutarXPath(String colPath, String docName, String xpath) throws Exception {
        Collection col = ConexionXMLDB.conectar(colPath);
        try {
            XPathQueryService service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            String query = String.format("doc('%s')%s", docName, xpath);
            ResourceSet result = service.query(query);

            StringBuilder sb = new StringBuilder();
            ResourceIterator it = result.getIterator();
            while (it.hasMoreResources()) {
                Resource res = it.nextResource();
                sb.append(res.getContent()).append("\n");
            }
            return sb.toString().trim();
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }

    public static String ejecutarXQuery(String colPath, String xquery) throws Exception {
        Collection col = ConexionXMLDB.conectar(colPath);
        try {
            XPathQueryService service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            ResourceSet result = service.query(xquery);

            StringBuilder sb = new StringBuilder();
            ResourceIterator it = result.getIterator();
            while (it.hasMoreResources()) {
                Resource res = it.nextResource();
                sb.append(res.getContent()).append("\n");
            }
            return sb.toString().trim();
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }
}