package com.biblioteca.service;

import com.biblioteca.modelo.Direccion;
import com.biblioteca.modelo.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UsuarioDireccionTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    // 1. Levantar la fábrica una sola vez antes de todos los tests
    @BeforeAll
    static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("mi-unidad-persistencia");
    }

    // 2. Cerrar la fábrica al terminar todo
    @AfterAll
    static void tearDownClass() {
        if (emf != null) emf.close();
    }

    // 3. Antes de cada test: crear EM y abrir transacción
    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    // 4. Después de cada test: rollback (para limpiar BD) y cerrar EM
    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            // Hacemos rollback para que los datos de un test no ensucien el siguiente
            em.getTransaction().rollback();
        }
        em.close();
    }

    // ---------------------------------------------------------------
    // TUS TESTS (Integrados)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Persistir usuario con dirección embebida")
    void testUsuarioConDireccion() {
        // Arrange
        Direccion direccion = new Direccion("Gran Vía 100", "Madrid", "28013", "España");

        Usuario usuario = new Usuario();
        usuario.setNombre("Juan Test");
        usuario.setEmail("juan." + UUID.randomUUID() + "@email.com"); // UUID evita colisiones
        usuario.setDireccion(direccion);

        // Act - Persistir
        em.persist(usuario);
        em.flush(); // Fuerza el SQL INSERT
        Long id = usuario.getId();

        em.clear(); // VITAL: Limpiar caché para probar que se guardó en BD real

        // Assert - Recuperar
        Usuario recuperado = em.find(Usuario.class, id); // Ejecuta SQL SELECT

        assertNotNull(recuperado);
        assertNotNull(recuperado.getDireccion());
        assertEquals("Madrid", recuperado.getDireccion().getCiudad());
        assertEquals("28013", recuperado.getDireccion().getCodigoPostal());
    }

    @Test
    @DisplayName("Modificar dirección de usuario")
    void testModificarDireccion() {
        // Arrange
        Usuario usuario = crearUsuarioConDireccion();
        em.persist(usuario);
        em.flush();
        Long id = usuario.getId();

        // Act - Modificar (JPA detecta cambios en el objeto embebido)
        usuario.getDireccion().setCiudad("Barcelona");
        usuario.getDireccion().setCodigoPostal("08001");

        em.flush(); // Fuerza SQL UPDATE
        em.clear(); // Desconecta objetos

        // Assert
        Usuario actualizado = em.find(Usuario.class, id);
        assertEquals("Barcelona", actualizado.getDireccion().getCiudad());
        assertEquals("08001", actualizado.getDireccion().getCodigoPostal());
    }

    // ---------------------------------------------------------------
    // NUEVO TEST RECOMENDADO: Query por campo embebido
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Buscar usuario filtrando por campo de dirección (JPQL)")
    void testBuscarPorCiudad() {
        // Arrange
        Usuario u1 = crearUsuarioConDireccion();
        u1.getDireccion().setCiudad("Valencia");
        em.persist(u1);

        Usuario u2 = crearUsuarioConDireccion();
        u2.getDireccion().setCiudad("Sevilla");
        em.persist(u2);

        em.flush();
        em.clear();

        // Act - JPQL permite navegar con el punto: u.direccion.ciudad
        String jpql = "SELECT u FROM Usuario u WHERE u.direccion.ciudad = :ciudad";
        Usuario encontrado = em.createQuery(jpql, Usuario.class)
                .setParameter("ciudad", "Valencia")
                .getSingleResult();

        // Assert
        assertEquals(u1.getId(), encontrado.getId());
        assertEquals("Valencia", encontrado.getDireccion().getCiudad());
    }

    // Helper
    private Usuario crearUsuarioConDireccion() {
        // Uso UUID para garantizar email único incluso en tests ultra-rápidos
        Direccion dir = new Direccion("Calle Test", "Madrid", "28000", "España");
        Usuario u = new Usuario();
        u.setNombre("Test");
        u.setEmail("test." + UUID.randomUUID() + "@email.com");
        u.setDireccion(dir);
        return u;
    }
}