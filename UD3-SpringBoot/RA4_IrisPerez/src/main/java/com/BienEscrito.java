package com;

import com.biblioteca.modelo.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class BienEscrito {

    // Factory como singleton (se crea una vez)
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("mi-unidad");

    public void buscarProducto(Long id) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            Producto p = em.find(Producto.class, id);

            if (p != null) {
                System.out.println(p.getNombre());
            } else {
                System.out.println("Producto no encontrado: " + id);
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    // Método para cerrar al finalizar la aplicación
    public static void cerrarFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}