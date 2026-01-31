package Actividad5;

import Actividad2.ConexionXMLDB;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;

public class LibroService {

    private static final String COL = "/db/biblioteca";
    private static final String DOC = "/db/biblioteca/biblioteca.xml";

    private void ejecutarUpdate(String xquery) throws Exception {
        Collection col = ConexionXMLDB.conectar(COL);
        try {
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.query(xquery);
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }

    // 1. Crear libro
    public void crear(String id, String titulo, String autor, int anio, boolean disponible) throws Exception {
        String xquery = String.format("update insert <libro id=\"%s\"><titulo>%s</titulo><autor>%s</autor><anio>%d</anio><disponible>%s</disponible></libro> into doc('%s')//biblioteca", id, titulo, autor, anio, disponible, DOC);
        ejecutarUpdate(xquery);
        System.out.println("Libro creado con id: " + id);
    }

    // 2. Actualizar campo
    public void actualizar(String id, String campo, String valor) throws Exception {
        String xquery = String.format("update value doc('%s')//libro[@id='%s']/%s with '%s'", DOC, id, campo, valor);
        ejecutarUpdate(xquery);
        System.out.println("Campo '" + campo + "' del libro con id '" + id + "' actualizado a " + valor);
    }

    // 3. Eliminar libro
    public void eliminar(String id) throws Exception {
        String xquery = String.format("update delete doc('%s')//libro[@id='%s']", DOC, id);
        ejecutarUpdate(xquery);
        System.out.println("Libro con id '" + id + "' eliminado");
    }

    // 4. Prestar libro
    public void prestar(String id) throws Exception {
        actualizar(id, "disponible", "false");
        System.out.println("Libro con id '" + id + "' prestado");
    }

    // 5. Devolver libro
    public void devolver(String id) throws Exception {
        actualizar(id, "disponible", "true");
        System.out.println("Libro con id '" + id + "' devuelto");
    }

    public static void main(String[] args) throws Exception {
        LibroService service = new LibroService();

        // Crear
        service.crear("L010", "Nuevo Libro", "Autor Test", 2025, true);

        // Prestar
        service.prestar("L010");

        // Actualizar titulo
        service.actualizar("L010", "titulo", "Titulo Modificado");

        // Devolver
        service.devolver("L010");

        // Eliminar
        service.eliminar("L010");
    }
}