package com.dam.accesodatos.recuperacionra3_irisperez.repository;

import com.dam.accesodatos.recuperacionra3_irisperez.entity.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CamionRepository extends JpaRepository<Camion, Long> {

    // Obtener camiones activos (activo = true)
    List<Camion> findByActivoTrue();
}
