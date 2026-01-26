package com.biblioteca.crud;

import com.biblioteca.modelo.EstadoProducto;
import com.biblioteca.modelo.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.util.List;

public class ProductoCRUD {

    private EntityManager em;

    public ProductoCRUD(EntityManager em) {
        this.em = em;
    }

    // 1. INSERT
    public void insertar() {
        em.getTransaction().begin();

        Producto p = new Producto(1, "Laptop", new BigDecimal("999.99"), 10);
        em.persist(p);

        em.getTransaction().commit();
    }

    // 2. SELECT disponibles
    public List<Producto> buscarDisponibles() {
        return em.createQuery(
                        "SELECT p FROM Producto p " +
                                "WHERE p.stock > 0 AND p.estado = :estado",
                        Producto.class)
                .setParameter("estado", EstadoProducto.ACTIVO)
                .getResultList();
    }

    // 3. UPDATE precio
    public void actualizarPrecio(Integer codigo, BigDecimal nuevoPrecio) {
        em.getTransaction().begin();

        TypedQuery<Producto> query = em.createQuery(
                "SELECT p FROM Producto p WHERE p.codigo = :codigo",
                Producto.class);
        query.setParameter("codigo", codigo);

        Producto p = query.getSingleResult();
        p.setPrecio(nuevoPrecio);

        em.getTransaction().commit();
    }

    // 4. DELETE sin stock
    public int eliminarSinStock() {
        em.getTransaction().begin();

        int eliminados = em.createQuery(
                        "DELETE FROM Producto p WHERE p.stock = 0")
                .executeUpdate();

        em.getTransaction().commit();
        return eliminados;
    }
}
