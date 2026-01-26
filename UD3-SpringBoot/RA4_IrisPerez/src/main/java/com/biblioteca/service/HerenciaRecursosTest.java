package com.biblioteca.service;

import com.biblioteca.modelo.Libro;
import com.biblioteca.modelo.Recurso;
import com.biblioteca.modelo.Revista;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HerenciaRecursosTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("mi-unidad-persistencia");
    }

    @AfterAll
    static void tearDownClass() {
        if (emf != null) emf.close();
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }

    // -------------------------------------------------------------
    // TUS TESTS DE HERENCIA
    // -------------------------------------------------------------

    @Test
    @DisplayName("Persistir y recuperar subtipos de Recurso (Polimorfismo)")
    void testHerenciaRecursos() {
        // Arrange
        Libro libro = crearLibro();
        Revista revista = crearRevista();

        // Act
        em.persist(libro);
        em.persist(revista);

        em.flush(); // Hibernate hace INSERT en 'recursos', luego en 'libros'/'revistas'
        em.clear(); // Limpiamos caché

        // Assert - Consulta polimórfica
        // JPQL: "FROM Recurso" traduce a un SQL con LEFT JOINs a las tablas hijas
        List<Recurso> recursos = em.createQuery(
                        "SELECT r FROM Recurso r ORDER BY r.titulo", Recurso.class)
                .getResultList();

        assertEquals(2, recursos.size());

        // Verificar tipos (Java puro)
        long numLibros = recursos.stream().filter(r -> r instanceof Libro).count();
        long numRevistas = recursos.stream().filter(r -> r instanceof Revista).count();

        assertEquals(1, numLibros);
        assertEquals(1, numRevistas);

        // Verificación extra: Acceso a datos específicos
        Libro libroRecuperado = (Libro) recursos.stream()
                .filter(r -> r instanceof Libro).findFirst().orElseThrow();
        assertEquals("Cervantes", libroRecuperado.getAutor()); // Dato que vive en la tabla 'libros'
    }

    @Test
    @DisplayName("Filtrar por tipo específico usando TYPE()")
    void testFiltrarPorTipo() {
        // Arrange
        em.persist(crearLibro());
        em.persist(crearRevista());
        em.flush();
        em.clear();

        // Act - filtrar solo libros
        // TYPE(r) es una función estándar de JPQL que examina el discriminador o la clase
        List<Recurso> soloLibros = em.createQuery(
                        "SELECT r FROM Recurso r WHERE TYPE(r) = Libro", Recurso.class)
                .getResultList();

        // Assert
        assertEquals(1, soloLibros.size());
        assertTrue(soloLibros.get(0) instanceof Libro);
        assertFalse(soloLibros.get(0) instanceof Revista);
    }

    // -------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------
    private Libro crearLibro() {
        Libro libro = new Libro();
        libro.setTitulo("Don Quijote");
        libro.setAnio(1605);
        libro.setDisponible(true);
        libro.setIsbn("978-84-123");
        libro.setAutor("Cervantes");
        libro.setPaginas(1345);
        return libro;
    }

    private Revista crearRevista() {
        Revista revista = new Revista();
        revista.setTitulo("National Geographic");
        revista.setAnio(2024);
        revista.setDisponible(true);
        revista.setIssn("0027-9358");
        revista.setNumero(245);
        revista.setMes("Enero");
        return revista;
    }
}