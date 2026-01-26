package com.biblioteca.consulta;

import com.biblioteca.modelo.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class GestorEliminacion {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("mi-unidad-persistencia");

    public void eliminarDatos() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            // ---------------------------------------------------------
            // 1. ELIMINAR ENTIDAD POR ID (Standard JPA)
            // ---------------------------------------------------------
            tx.begin();

            // Primero buscamos la entidad para adjuntarla al contexto de persistencia
            Usuario usuario = em.find(Usuario.class, 1L);

            if (usuario != null) {
                // em.remove marca el objeto para ser borrado.
                // El SQL DELETE real se ejecuta al hacer commit.
                // VENTJA: Esto SÍ dispara los eventos de ciclo de vida (@PreRemove) 
                // y las cascadas (CascadeType.REMOVE) definidas en Java.
                em.remove(usuario);
                System.out.println("Usuario 1 marcado para eliminación.");
            } else {
                System.out.println("El usuario 1 no existe.");
            }

            tx.commit();


            // ---------------------------------------------------------
            // 2. BULK DELETE (Borrado Masivo con JPQL)
            // ---------------------------------------------------------
            tx.begin();

            // Esto se traduce directamente a SQL DELETE.
            // DESVENTAJA: Salta la caché de Hibernate y NO ejecuta CascadeType.REMOVE de Java.
            // (Solo funcionarán los ON DELETE CASCADE definidos en la base de datos PostgreSQL).
            int eliminados = em.createQuery(
                            "DELETE FROM Recurso r WHERE r.disponible = :estado")
                    .setParameter("estado", false)
                    .executeUpdate();

            System.out.println("Recursos no disponibles eliminados: " + eliminados);

            tx.commit();

            // IMPORTANTE:
            // Al hacer un Bulk Delete, el contexto de persistencia (la caché de 1er nivel)
            // puede quedarse con objetos en memoria que ya no existen en la BBDD.
            // Limpiamos la caché para evitar inconsistencias "Zombie".
            em.clear();

        } catch (Exception e) {
            // Si algo falla, deshacemos los cambios pendientes
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            // Siempre cerrar el EntityManager
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}