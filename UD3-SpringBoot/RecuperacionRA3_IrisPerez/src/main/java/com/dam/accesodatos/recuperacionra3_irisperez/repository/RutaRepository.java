package com.dam.accesodatos.recuperacionra3_irisperez.repository;

import com.dam.accesodatos.recuperacionra3_irisperez.entity.Camion;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RutaRepository extends JpaRepository<Ruta, Long> {
    // Obtener rutas activas (activo = true)
    List<Ruta> findByActivaTrue();
}
