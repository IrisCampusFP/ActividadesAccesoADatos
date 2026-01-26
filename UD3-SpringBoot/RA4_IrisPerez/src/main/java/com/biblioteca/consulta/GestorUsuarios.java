package com.biblioteca.consulta;

import com.biblioteca.modelo.Direccion;
import com.biblioteca.modelo.EstadoProducto;
import com.biblioteca.modelo.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class GestorUsuarios {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("mi-unidad-persistencia");

    public void ejecutarOperaciones() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            // ---------------------------------------------------------
            // 1. MODIFICAR DIRECCIÓN (Objeto Embebido)
            // ---------------------------------------------------------
            tx.begin();

            Usuario usuario = em.find(Usuario.class, 1L);

            if (usuario != null) {
                // Opción A: Modificar campos individuales
                // JPA detecta que 'usuario' está gestionado y hará el UPDATE al hacer commit
                usuario.getDireccion().setCiudad("Barcelona");
                usuario.getDireccion().setCodigoPostal("08001");

                // Opción B: Reemplazar el objeto completo
                Direccion nuevaDir = new Direccion("Nueva Calle", "Barcelona", "08001", "España");
                usuario.setDireccion(nuevaDir);
            }

            tx.commit();
            System.out.println("Dirección actualizada correctamente.");


            // ---------------------------------------------------------
            // 2. MANIPULAR COLECCIÓN DE TELÉFONOS
            // ---------------------------------------------------------
            tx.begin();

            // Es buena práctica volver a buscar la entidad en una nueva transacción
            Usuario u = em.find(Usuario.class, 1L);

            if (u != null) {
                // Agregar
                u.getTelefonos().add("600123456");

                // Eliminar
                u.getTelefonos().remove("666111222");

                // Reemplazar toda la colección
                // Hibernate gestiona esto borrando los registros viejos e insertando los nuevos
                u.getTelefonos().clear();
                u.getTelefonos().addAll(List.of("611111111", "622222222"));
            }

            tx.commit();
            System.out.println("Teléfonos actualizados correctamente.");


            // ---------------------------------------------------------
            // 3. UPDATE MASIVO CON JPQL
            // ---------------------------------------------------------
            tx.begin();

            int actualizados = em.createQuery(
                            "UPDATE Producto p SET p.precio = p.precio * 1.10 " +
                                    "WHERE p.estado = :estado")
                    .setParameter("estado", EstadoProducto.ACTIVO) // Asumiendo que tienes un Enum
                    .executeUpdate();

            System.out.println("Productos actualizados: " + actualizados);

            tx.commit();

            // IMPORTANTE: Limpiar caché de primer nivel tras bulk update.
            // Esto evita que el EntityManager tenga datos obsoletos en memoria
            // que no coincidan con lo que acabamos de actualizar directamente en BBDD.
            em.clear();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
