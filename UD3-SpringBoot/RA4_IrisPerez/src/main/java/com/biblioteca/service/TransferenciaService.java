package com.biblioteca.service;

import com.biblioteca.modelo.Cuenta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

import java.math.BigDecimal;

public class TransferenciaService {

    private EntityManagerFactory emf;

    public TransferenciaService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void transferir(Long cuentaOrigenId,
                           Long cuentaDestinoId,
                           BigDecimal cantidad) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Obtener cuentas con bloqueo pesimista
            Cuenta origen = em.find(Cuenta.class, cuentaOrigenId,
                    LockModeType.PESSIMISTIC_WRITE);
            Cuenta destino = em.find(Cuenta.class, cuentaDestinoId,
                    LockModeType.PESSIMISTIC_WRITE);

            // Validaciones
            if (origen == null || destino == null) {
                throw new IllegalArgumentException("Cuenta no encontrada");
            }

            if (origen.getSaldo().compareTo(cantidad) < 0) {
                throw new IllegalStateException("Saldo insuficiente");
            }

            // Realizar transferencia
            origen.setSaldo(origen.getSaldo().subtract(cantidad));
            destino.setSaldo(destino.getSaldo().add(cantidad));

            tx.commit();
            System.out.println("Transferencia completada");

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.err.println("Transferencia fallida: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }


}