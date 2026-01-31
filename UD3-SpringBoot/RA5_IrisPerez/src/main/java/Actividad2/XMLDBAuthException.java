package Actividad2;

/** Excepción específica para fallos de usuario/contraseña */
public class XMLDBAuthException extends XMLDBConnectionException {
    public XMLDBAuthException(String message) {
        super(message);
    }
}