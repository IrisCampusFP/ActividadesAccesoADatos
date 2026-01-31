package Actividad5;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OperacionesDelete {

    private static final String EXIST_URL = "http://localhost:8080/exist/rest/db";
    private static final String USER = "admin";
    private static final String PASS = "";

    private void ejecutarUpdate(String xquery, String operacion) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String auth = Base64.getEncoder().encodeToString((USER + ":" + PASS).getBytes());

        String wrappedQuery = String.format("""
            <query xmlns="http://exist.sourceforge.net/NS/exist">
                <text><![CDATA[%s]]></text>
            </query>
            """, xquery);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EXIST_URL))
                .header("Authorization", "Basic " + auth)
                .header("Content-Type", "application/xml")
                .POST(HttpRequest.BodyPublishers.ofString(wrappedQuery, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println("[OK] " + operacion);
        } else {
            System.out.println("[ERROR " + response.statusCode() + "] " + operacion);
            System.out.println("Respuesta: " + response.body());
        }
    }

    public void eliminarLibro(String idLibro) throws Exception {
        String xquery = String.format("update delete doc('/db/biblioteca/biblioteca.xml')//libro[@id='%s']", idLibro);
        ejecutarUpdate(xquery, "Eliminar libro " + idLibro);
    }

    public void eliminarAtributoIsbn(String idLibro) throws Exception {
        String xquery = String.format("update delete doc('/db/biblioteca/biblioteca.xml')//libro[@id='%s']/@isbn", idLibro);
        ejecutarUpdate(xquery, "Eliminar atributo isbn de " + idLibro);
    }

    public void eliminarGeneroClasico() throws Exception {
        String xquery = "update delete doc('/db/biblioteca/biblioteca.xml')//libro/generos/genero[. = 'Clasico']";
        ejecutarUpdate(xquery, "Eliminar genero Clasico de todos los libros");
    }

    public void eliminarLibrosNoDisponiblesAntiguos() throws Exception {
        String xquery = "update delete doc('/db/biblioteca/biblioteca.xml')//libro[disponible='false' and anio < 1950]";
        ejecutarUpdate(xquery, "Eliminar libros no disponibles anteriores a 1950");
    }

    public static void main(String[] args) throws Exception {
        OperacionesDelete op = new OperacionesDelete();
        op.eliminarLibro("L005");
        op.eliminarAtributoIsbn("L003");
        op.eliminarGeneroClasico();
        op.eliminarLibrosNoDisponiblesAntiguos();
    }
}