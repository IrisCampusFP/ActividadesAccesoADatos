package com.biblioteca.service;

import com.biblioteca.modelo.Producto;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

public class ServicioInventario {

    private EntityManagerFactory emf;

    public ServicioInventario(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void reservarStock(Long productoId, int cantidad) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Configuración del Timeout:
            // Si la fila está bloqueada por otro, esperamos máximo 3 segundos (3000 ms).
            // Si no se libera en ese tiempo, lanza LockTimeoutException.
            Map<String, Object> hints = new HashMap<>();
            hints.put("jakarta.persistence.lock.timeout", 3000L); // En milisegundos

            // OPCIÓN 1 (Mejorada): Find con bloqueo y timeout
            System.out.println("Intentando adquirir bloqueo físico en BBDD...");

            Producto producto = em.find(Producto.class, productoId,
                    LockModeType.PESSIMISTIC_WRITE,
                    hints);

            if (producto == null) {
                throw new IllegalArgumentException("Producto no encontrado");
            }

            // A partir de aquí, NADIE más puede escribir en esta fila 
            // hasta que hagamos commit o rollback.
            System.out.println("Bloqueo adquirido. Modificando stock...");

            if (producto.getStock() < cantidad) {
                throw new RuntimeException("Stock insuficiente");
            }

            // Simulamos un proceso lento para probar que otros hilos se bloquean
            try { Thread.sleep(1000); } catch (InterruptedException e) {}

            producto.setStock(producto.getStock() - cantidad);

            tx.commit(); // Aquí se libera el bloqueo en la BBDD
            System.out.println("Transacción finalizada y bloqueo liberado.");

        } catch (LockTimeoutException e) {
            // Capturamos específicamente el error de tiempo de espera
            if (tx.isActive()) tx.rollback();
            System.err.println("Error: El recurso está ocupado por otra transacción. Inténtelo más tarde.");

        } catch (PessimisticLockException e) {
            // Error más grave de bloqueo (ej. Deadlock)
            if (tx.isActive()) tx.rollback();
            System.err.println("Error grave de bloqueo en base de datos.");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}