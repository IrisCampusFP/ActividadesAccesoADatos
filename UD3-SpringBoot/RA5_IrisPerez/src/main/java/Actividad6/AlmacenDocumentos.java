package Actividad6;// AlmacenDocumentos.java
import Actividad2.ConexionXMLDB;
import Actividad2.XMLDBNotFoundException;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;

public class AlmacenDocumentos {
    
    public static void guardarDesdeString(String colPath, String nombre, String contenido) throws Exception {
        Collection col = ConexionXMLDB.conectar(colPath);
        try {
            XMLResource resource = (XMLResource) col.createResource(nombre, XMLResource.RESOURCE_TYPE);
            resource.setContent(contenido);
            col.storeResource(resource);
            System.out.println("Documento guardado: " + nombre);
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }
    
    public static String obtenerContenido(String colPath, String nombre) throws Exception {
        Collection col = ConexionXMLDB.conectar(colPath);
        try {
            Resource resource = col.getResource(nombre);
            if (resource == null) {
                throw new XMLDBNotFoundException(colPath + "/" + nombre);
            }
            return (String) resource.getContent();
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }
}