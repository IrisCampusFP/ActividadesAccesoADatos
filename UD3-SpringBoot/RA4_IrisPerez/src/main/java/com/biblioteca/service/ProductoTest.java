package com.biblioteca.service;

import com.biblioteca.modelo.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductoTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    static void initFactory() {
        emf = Persistence.createEntityManagerFactory("test-unit");
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        // Rollback para aislar tests
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }

    @AfterAll
    static void closeFactory() {
        if (emf != null) emf.close();
    }

    @Test
    @DisplayName("Persistir producto genera ID automáticamente")
    void testPersistirProductoGeneraId() {
        // Arrange
        Producto producto = new Producto();
        producto.setCodigo(1);
        producto.setNombre("Laptop Test");
        producto.setPrecio(new BigDecimal("999.99"));
        producto.setStock(10);

        // Act
        em.persist(producto);
        em.flush();

        // Assert
        assertNotNull(producto.getId(), "El ID debe generarse");
        assertTrue(producto.getId() > 0, "El ID debe ser positivo");
    }
}