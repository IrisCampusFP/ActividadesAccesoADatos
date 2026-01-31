package Actividad5;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ModificacionesCondicionales {

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

    public void marcarClasicos() throws Exception {
        String xquery = "for $libro in doc('/db/biblioteca/biblioteca.xml')//libro[anio < 1970] where not($libro/@clasico) return update insert attribute clasico { 'true' } into $libro";
        ejecutarUpdate(xquery, "Marcar libros anteriores a 1970 como clasicos");
    }

    public void incrementarPrestamos(String idLibro) throws Exception {
        String xquery = String.format("""
            let $libro := doc('/db/biblioteca/biblioteca.xml')//libro[@id='%s']
            let $prestamos := if ($libro/@prestamos) then xs:integer($libro/@prestamos) else 0
            return if ($libro/@prestamos) then update value $libro/@prestamos with $prestamos + 1 else update insert attribute prestamos { '1' } into $libro
            """, idLibro);
        ejecutarUpdate(xquery, "Incrementar prestamos de " + idLibro);
    }

    public void actualizarDisponibilidadPorCopias() throws Exception {
        String xquery = "for $libro in doc('/db/biblioteca/biblioteca.xml')//libro[@copias = '0'] return update value $libro/disponible with 'false'";
        ejecutarUpdate(xquery, "Actualizar disponibilidad de libros sin copias");
    }

    public static void main(String[] args) throws Exception {
        ModificacionesCondicionales mc = new ModificacionesCondicionales();
        mc.marcarClasicos();
        mc.incrementarPrestamos("L001");
        mc.actualizarDisponibilidadPorCopias();
    }
}