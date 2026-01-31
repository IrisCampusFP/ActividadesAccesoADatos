package Actividad2;

/** Excepción específica para cuando la colección no existe */
public class XMLDBNotFoundException extends XMLDBConnectionException {
    public XMLDBNotFoundException(String path) {
        super("La colección solicitada no existe: " + path);
    }
}
