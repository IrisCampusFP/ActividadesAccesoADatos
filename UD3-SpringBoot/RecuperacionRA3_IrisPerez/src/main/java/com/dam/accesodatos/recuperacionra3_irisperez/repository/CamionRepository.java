package com.dam.accesodatos.recuperacionra3_irisperez.repository;

import com.dam.accesodatos.recuperacionra3_irisperez.entity.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO: CamionRepository
 * Interfaz que proporciona acceso a datos para la entidad Camion.

 * Al extender JpaRepository, heredamos automáticamente métodos CRUD:
 * - save(entity): Guarda o actualiza un camion
 * - findById(id): Busca un camion por su ID
 * - findAll(): Obtiene todos los camions
 * - deleteById(id): Elimina un camion por su ID
 * - count(): Cuenta el total de camions
 * - existsById(id): Verifica si existe un camion

 * Spring Data JPA implementa automáticamente esta interfaz
 * en tiempo de ejecución (no necesitamos escribir código SQL).
 */
@Repository
public interface CamionRepository extends JpaRepository<Camion, Long> {

    // Buscar camion por username
    Optional<Camion> findByUsername(String username);

    // Buscar camion por email
    Optional<Camion> findByEmail(String email);

    // Obtener camions activos (activo = true)
    List<Camion> findByActivoTrue();

    // Comprobar si existe un camion con username
    boolean existsByUsername(String username);

    // Comprobar si existe un camion con email
    boolean existsByEmail(String email);

    // Obtener camions con rol 'MEDICO'
    @Query("SELECT u FROM Camion u JOIN u.roles r WHERE r.nombre = 'MEDICO'")
    List<Camion> obtenerCamionesMedico();
}
