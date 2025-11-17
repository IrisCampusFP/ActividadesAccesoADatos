package controller;

import model.Genero;
import model.Libro;
import service.TransaccionesService;
import view.Menus;

import java.sql.SQLException;
import java.util.ArrayList;

import static utils.Utils.*;

public class ControladorPrincipal {

    private Menus m = new Menus();
    private ControladorLibros cl = new ControladorLibros();
    private TransaccionesService transacciones = new TransaccionesService();

    public void menuPrincipal() throws SQLException {
        while (true) {
            m.menuPrincipal();
            int opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    cl.menuLibros();
                    break;
                case 2:
                    insertarGeneroYLibros();
                    break;
                case 3:
                    //cl.;
                    break;
                case 0:
                    System.out.println("Cerrando programa... ¡Hasta pronto!");
                    sc.close();
                    return;
                default:
                    opcionInvalida();
            }
            pausar();
        }
    }

    public void insertarGeneroYLibros() throws SQLException {
        Genero genero = new Genero(0, "Acción");
        ArrayList <Libro> libros = new ArrayList<Libro>();
        libros.add(new Libro(0, "Libro añadido con transacción", "Iris Pérez", 20, 300, genero));
        libros.add(new Libro(0, "Libro añadido con transacción 2", "Iris Pérez", 18, 34, genero));
        libros.add(new Libro(0, "Libro añadido con transacción 3", "Iris Pérez", 15, 56, genero));
        transacciones.insertarGeneroYLibros(genero, libros);
    }
}