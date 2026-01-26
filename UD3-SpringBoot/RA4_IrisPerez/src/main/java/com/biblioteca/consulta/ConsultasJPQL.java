package com.biblioteca.consulta;

import com.biblioteca.modelo.Libro;
import com.biblioteca.modelo.Recurso;
import com.biblioteca.modelo.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ConsultasJPQL {

    private final EntityManager em;

    public ConsultasJPQL(EntityManager em) {
        this.em = em;
    }

    // 1. Usuarios de una ciudad (objeto embebido)
    public List<Usuario> usuariosPorCiudad(String ciudad) {
        TypedQuery<Usuario> q = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.direccion.ciudad = :ciudad",
                Usuario.class);
        q.setParameter("ciudad", ciudad);
        return q.getResultList();
    }

    // 2. Usuarios que tengan un teléfono específico (MEMBER OF)
    public List<Usuario> usuariosConTelefono(String telefono) {
        TypedQuery<Usuario> q = em.createQuery(
                "SELECT u FROM Usuario u WHERE :tel MEMBER OF u.telefonos",
                Usuario.class);
        q.setParameter("tel", telefono);
        return q.getResultList();
    }

    // 3. Consulta polimórfica de recursos
    public List<Recurso> todosRecursos() {
        TypedQuery<Recurso> q = em.createQuery(
                "SELECT r FROM Recurso r",
                Recurso.class);
        return q.getResultList();
    }

    // 4. Solo libros de un autor (LIKE con comodines gestionados por el llamante)
    public List<Libro> librosPorAutor(String patronAutor) {
        TypedQuery<Libro> q = em.createQuery(
                "SELECT l FROM Libro l WHERE l.autor LIKE :autor",
                Libro.class);
        q.setParameter("autor", patronAutor);
        return q.getResultList();
    }

    // Alternativa con TYPE() para filtrar por tipo
    public List<Recurso> soloLibrosConTYPE() {
        TypedQuery<Recurso> q = em.createQuery(
                "SELECT r FROM Recurso r WHERE TYPE(r) = Libro",
                Recurso.class);
        return q.getResultList();
    }

    public Usuario cargarUsuarioConTelefonos(Long usuarioId) {
        TypedQuery<Usuario> q = em.createQuery(
                "SELECT DISTINCT u FROM Usuario u " +
                        "LEFT JOIN FETCH u.telefonos " +
                        "WHERE u.id = :id",
                Usuario.class);
        q.setParameter("id", usuarioId);
        return q.getSingleResult();
    }

    // Variante: cargar todos los usuarios con sus teléfonos en una sola consulta
    public List<Usuario> cargarUsuariosConTelefonos() {
        return em.createQuery(
                "SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.telefonos",
                Usuario.class).getResultList();
    }

}