package com.biblioteca.consulta;

import com.biblioteca.modelo.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;

public class GestorMerge {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("mi-unidad-persistencia");

    public void demostracionMerge() {
        // Variable para guardar el objeto que quedará "desconectado"
        Producto productoDetached = null;

        // ---------------------------------------------------------
        // PASO 1: Obtener entidad y desconectarla (DETACH)
        // ---------------------------------------------------------
        EntityManager em1 = emf.createEntityManager();
        try {
            // El objeto nace como "Managed" (conectado a la BBDD)
            productoDetached = em1.find(Producto.class, 1L);

            System.out.println("Precio original: " + productoDetached.getPrecio());
        } finally {
            // Al cerrar el EM, todas las entidades cargadas pasan a estado "Detached"
            em1.close();
        }

        // ---------------------------------------------------------
        // PASO 2: Modificar la entidad "offline"
        // ---------------------------------------------------------
        // Esto solo cambia los valores en la memoria RAM de Java.
        // La base de datos NO sabe nada de esto aún.
        if (productoDetached != null) {
            productoDetached.setPrecio(new BigDecimal("1500.00"));
            productoDetached.setStock(productoDetached.getStock() + 10);
            System.out.println("Modificación en memoria realizada (aún no en BBDD).");
        }

        // ---------------------------------------------------------
        // PASO 3: Sincronizar cambios (MERGE)
        // ---------------------------------------------------------
        EntityManager em2 = emf.createEntityManager();
        EntityTransaction tx = em2.getTransaction();

        try {
            tx.begin();

            // EL MOMENTO CRÍTICO:
            // merge() busca la ID en la BBDD, trae una COPIA fresca y le pega tus cambios.
            // Devuelve esa NUEVA copia gestionada.
            Producto productoManaged = em2.merge(productoDetached);

            // DEMOSTRACIÓN: Son objetos diferentes en memoria
            boolean sonElMismo = (productoDetached == productoManaged);
            System.out.println("¿Es el mismo objeto en memoria? " + sonElMismo); // Imprimirá FALSE

            // Si quieres seguir haciendo cambios en ESTA transacción,
            // debes usar 'productoManaged', no el 'detached'.
            productoManaged.setNombre("Producto Actualizado y Renombrado");

            tx.commit(); // Aquí se ejecuta el UPDATE real en SQL
            System.out.println("Transacción confirmada.");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em2.close();
        }
    }
}