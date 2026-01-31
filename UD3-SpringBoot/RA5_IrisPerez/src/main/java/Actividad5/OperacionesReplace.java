package Actividad5;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OperacionesReplace {

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

    public void cambiarTitulo(String idLibro, String nuevoTitulo) throws Exception {
        String xquery = String.format("update value doc('/db/biblioteca/biblioteca.xml')//libro[@id='%s']/titulo with '%s'", idLibro, nuevoTitulo);
        ejecutarUpdate(xquery, "Cambiar titulo de " + idLibro);
    }

    public void actualizarDisponibilidad(String idLibro, String disponibilidad) throws Exception {
        String xquery = String.format("update value doc('/db/biblioteca/biblioteca.xml')//libro[@id='%s']/disponible with '%s'", idLibro, disponibilidad);
        ejecutarUpdate(xquery, "Actualizar disponibilidad de " + idLibro);
    }

    public void reemplazarAutorConEstructura(String idLibro, String nombre, String apellido) throws Exception {
        String xquery = String.format("update replace doc('/db/biblioteca/biblioteca.xml')//libro[@id='%s']/autor with <autor><nombre>%s</nombre><apellido>%s</apellido></autor>", idLibro, nombre, apellido);
        ejecutarUpdate(xquery, "Reemplazar autor de " + idLibro);
    }

    public void corregirAnio(String idLibro, String nuevoAnio) throws Exception {
        String xquery = String.format("update value doc('/db/biblioteca/biblioteca.xml')//libro[@id='%s']/anio with '%s'", idLibro, nuevoAnio);
        ejecutarUpdate(xquery, "Corregir anio de " + idLibro);
    }

    public static void main(String[] args) throws Exception {
        OperacionesReplace op = new OperacionesReplace();
        op.cambiarTitulo("L001", "El ingenioso hidalgo Don Quijote de la Mancha");
        op.actualizarDisponibilidad("L003", "true");
        op.reemplazarAutorConEstructura("L001", "Miguel", "de Cervantes Saavedra");
        op.corregirAnio("L004", "1954");
    }
}