import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LecturaFicheroScanner {
    public static void main(String[] args) {
        File archivo = new File("productos.txt");

        System.out.print("ARCHIVO '" + archivo + "': ");

        if (!archivo.exists()) {
            System.out.println("El fichero no existe.");
            return;
        } else {
            System.out.println("El fichero ocupa " + archivo.length() + " bytes.");
        }

        System.out.println("------------------------------------------------------------------");

        int totalArticulos = 0;
        double sumaPrecios = 0.0;
        double importeTotal = 0.0;

        try {
            Scanner sc = new Scanner(archivo, StandardCharsets.UTF_8);
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                String[] campos = linea.split(";");
                String categoria = campos[0];
                String nombre = campos[1];
                double precio = Double.parseDouble(campos[2]);
                int stock = Integer.parseInt(campos[3]);

                System.out.println(nombre + "(" + categoria + ") -- Precio: " + precio + "€ -- Stock: " + stock);

                totalArticulos++; // Sumo un artículo al total de artículos
                sumaPrecios += precio; // Sumo el precio del artículo al total de precios
                importeTotal += precio * stock; // Sumo el importe (precio * stock) al importe total
            }
        } catch (IOException e) {
            System.out.println("No se pudo abrir el archivo: " + e.getMessage());
        }

        double promedioPrecios;
        if (totalArticulos > 0) {
            promedioPrecios = sumaPrecios / totalArticulos;
        } else {
            promedioPrecios = 0;
        }

        System.out.println("------------------------------------------------------------------");
        System.out.println("DATOS:");
        System.out.println("------");

        System.out.println("- Número total de artículos: " + totalArticulos);
        System.out.printf("- Promedio de precios: %.2f €%n", promedioPrecios);
        System.out.printf("- Importe total: %.2f €%n", importeTotal);
    }
}