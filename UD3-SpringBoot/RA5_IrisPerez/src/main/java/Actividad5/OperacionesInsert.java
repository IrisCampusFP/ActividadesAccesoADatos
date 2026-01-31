package Actividad5;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OperacionesInsert {

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

    public void insertarNuevoLibro() throws Exception {
        String xquery = """
            let $nuevoLibro := 
                <libro id="L005" isbn="978-84-204-8318-1">
                    <titulo>La sombra del viento</titulo>
                    <autor>Carlos Ruiz Zafon</autor>
                    <anio>2001</anio>
                    <generos>
                        <genero>Novela</genero>
                        <genero>Misterio</genero>
                    </generos>
                    <paginas>575</paginas>
                    <disponible>true</disponible>
                </libro>
            return update insert $nuevoLibro into doc('/db/biblioteca/biblioteca.xml')/biblioteca
            """;
        ejecutarUpdate(xquery, "Insertar nuevo libro");
    }

    public void anadirGenero(String idLibro, String genero) throws Exception {
        String xquery = String.format("""
            update insert <genero>%s</genero> 
            into doc('/db/biblioteca/biblioteca.xml')//libro[@id='%s']/generos
            """, genero, idLibro);
        ejecutarUpdate(xquery, "Anadir genero '" + genero + "' a " + idLibro);
    }

    public void anadirAtributoEdicion(String idLibro, String edicion) throws Exception {
        String xquery = String.format("""
            update insert attribute edicion { '%s' } 
            into doc('/db/biblioteca/biblioteca.xml')//libro[@id='%s']
            """, edicion, idLibro);
        ejecutarUpdate(xquery, "Anadir atributo edicion a " + idLibro);
    }

    public void insertarEditorialAntesAnio(String idLibro, String editorial) throws Exception {
        String xquery = String.format("update insert <editorial>%s</editorial> preceding doc('/db/biblioteca/biblioteca.xml')//libro[@id='%s']/anio", editorial, idLibro);
        ejecutarUpdate(xquery, "Insertar editorial antes de anio en " + idLibro);
    }

    public static void main(String[] args) throws Exception {
        OperacionesInsert op = new OperacionesInsert();
        op.insertarNuevoLibro();
        op.anadirGenero("L001", "Aventuras");
        op.anadirAtributoEdicion("L001", "2025");
        op.insertarEditorialAntesAnio("L001", "Catedra");
    }
}