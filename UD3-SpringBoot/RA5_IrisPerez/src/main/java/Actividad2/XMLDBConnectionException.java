package Actividad2;

/** Excepción padre para todos los errores de conexión */
public class XMLDBConnectionException extends Exception {
    public XMLDBConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
    public XMLDBConnectionException(String message) {
        super(message);
    }
}