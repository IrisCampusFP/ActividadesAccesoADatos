package com.dam.accesodatos.recuperacionra3_irisperez.repository;

import com.dam.accesodatos.recuperacionra3_irisperez.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    boolean existsByCamionIdAndRutaId(Long camionId, Long rutaId);
}
