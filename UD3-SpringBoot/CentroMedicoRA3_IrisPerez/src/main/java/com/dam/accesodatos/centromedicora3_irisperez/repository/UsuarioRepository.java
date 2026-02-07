package com.dam.accesodatos.centromedicora3_irisperez.repository;

import com.dam.accesodatos.centromedicora3_irisperez.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO: UsuarioRepository
 * Interfaz que proporciona acceso a datos para la entidad Usuario.
 *
 * Al extender JpaRepository, heredamos automáticamente métodos CRUD:
 * - save(entity): Guarda o actualiza un usuario
 * - findById(id): Busca un usuario por su ID
 * - findAll(): Obtiene todos los usuarios
 * - deleteById(id): Elimina un usuario por su ID
 * - count(): Cuenta el total de usuarios
 * - existsById(id): Verifica si existe un usuario
 *
 * Spring Data JPA implementa automáticamente esta interfaz
 * en tiempo de ejecución (no necesitamos escribir código SQL).
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por username
    Optional<Usuario> findByUsername(String username);

    // Buscar usuario por email
    Optional<Usuario> findByEmail(String email);

    // Obtener usuarios activos (activo = true)
    List<Usuario> findByActivoTrue();

    // Comprobar si existe un usuario con username
    boolean existsByUsername(String username);

    // Comprobar si existe un usuario con email
    boolean existsByEmail(String email);

    // Obtener usuarios con rol 'MEDICO'
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r.nombre = 'MEDICO'")
    List<Usuario> obtenerUsuariosMedico();
}
