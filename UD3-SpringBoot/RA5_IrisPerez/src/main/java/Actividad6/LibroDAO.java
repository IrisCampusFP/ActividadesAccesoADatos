package Actividad6;// LibroDAO.java
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Actividad2.ConexionXMLDB;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;

public class LibroDAO implements GenericXMLDAO<Libro> {
    
    private static final String COLECCION = "/db/biblioteca";
    private final JAXBService jaxbService;
    
    public LibroDAO() throws Exception {
        this.jaxbService = new JAXBService();
    }
    
    @Override
    public void guardar(Libro libro) throws Exception {
        jaxbService.guardar(COLECCION, libro);
    }
    
    @Override
    public Optional<Libro> buscarPorId(String id) throws Exception {
        try {
            Libro libro = jaxbService.recuperar(COLECCION, id);
            return Optional.of(libro);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Libro> buscarTodos() throws Exception {
        String xquery = String.format("for $doc in collection('%s') return $doc/libro", COLECCION);
        return ejecutarConsulta(xquery);
    }
    
    @Override
    public void actualizar(Libro libro) throws Exception {
        eliminar(libro.getId());
        guardar(libro);
    }
    
    @Override
    public void eliminar(String id) throws Exception {
        String nombre = "libro_" + id + ".xml";
        Collection col = ConexionXMLDB.conectar(COLECCION);
        try {
            Resource res = col.getResource(nombre);
            if (res != null) {
                col.removeResource(res);
            }
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }
    
    public List<Libro> buscarPorAutor(String autor) throws Exception {
        String xquery = String.format("for $doc in collection('%s') where contains($doc//autor, '%s') return $doc/libro", COLECCION, autor);
        return ejecutarConsulta(xquery);
    }
    
    public List<Libro> buscarDisponibles() throws Exception {
        String xquery = String.format("for $doc in collection('%s') where $doc//disponible = 'true' return $doc/libro", COLECCION);
        return ejecutarConsulta(xquery);
    }
    
    private List<Libro> ejecutarConsulta(String xquery) throws Exception {
        List<Libro> libros = new ArrayList<>();
        Collection col = ConexionXMLDB.conectar(COLECCION);
        try {
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            ResourceSet rs = service.query(xquery);
            ResourceIterator it = rs.getIterator();
            while (it.hasMoreResources()) {
                String xml = (String) it.nextResource().getContent();
                libros.add(jaxbService.fromXML(xml));
            }
        } finally {
            ConexionXMLDB.cerrar(col);
        }
        return libros;
    }
}