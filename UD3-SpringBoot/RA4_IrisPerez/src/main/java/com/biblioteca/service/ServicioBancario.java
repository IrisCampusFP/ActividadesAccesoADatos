package com.biblioteca.service;

import com.biblioteca.modelo.Cuenta;
import jakarta.persistence.*;

import java.math.BigDecimal;

public class ServicioBancario {

    private EntityManagerFactory emf;

    public ServicioBancario(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void actualizarSaldoConReintento(Long cuentaId, BigDecimal nuevoSaldo, int maxReintentos) {
        int intentos = 0;

        while (intentos < maxReintentos) {
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();

                // 1. Leer datos (obtiene versión actual, ej: 5)
                Cuenta cuenta = em.find(Cuenta.class, cuentaId);

                if (cuenta == null) throw new IllegalArgumentException("Cuenta no encontrada");

                // 2. Modificar (en memoria)
                System.out.println("Intento " + (intentos + 1) + " - Versión actual: " + cuenta.getVersion());
                cuenta.setSaldo(nuevoSaldo);

                // 3. Commit
                // JPA ejecuta: UPDATE cuentas SET saldo=?, version=6 WHERE id=? AND version=5
                // Si la versión en BD ya no es 5 (alguien más la cambió), filas actualizadas = 0.
                // Esto lanza OptimisticLockException.
                tx.commit();

                System.out.println("Actualización exitosa. Nueva versión: " + cuenta.getVersion());
                return; // SALIR DEL BUCLE

            } catch (RollbackException | OptimisticLockException e) {
                // NOTA: A veces JPA envuelve la OptimisticLockException en una RollbackException
                if (tx.isActive()) tx.rollback();

                intentos++;
                System.err.println("¡Conflicto de concurrencia! Alguien modificó el registro mientras leíamos.");

                if (intentos >= maxReintentos) {
                    throw new RuntimeException("Falló la actualización tras " + maxReintentos + " intentos.", e);
                }

                // Backoff: Esperar un poco antes de reintentar para no saturar
                try { Thread.sleep(100); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            } finally {
                em.close();
            }
        }
    }
}