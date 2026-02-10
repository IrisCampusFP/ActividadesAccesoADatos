package com.dam.accesodatos.recuperacionra3_irisperez.service;

import com.dam.accesodatos.recuperacionra3_irisperez.DTO.AsignacionDTO;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Asignacion;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Camion;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Ruta;
import com.dam.accesodatos.recuperacionra3_irisperez.repository.AsignacionRepository;
import com.dam.accesodatos.recuperacionra3_irisperez.repository.CamionRepository;
import com.dam.accesodatos.recuperacionra3_irisperez.repository.RutaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * SERVICIO: AsignacionService
 *
 * Anotaciones utilizadas:
 * - @Service: Marca la clase como un componente de servicio de Spring.
 * - @Transactional: Gestiona automáticamente las transacciones.
 */
@Service
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final CamionRepository camionRepository;
    private final RutaRepository rutaRepository;

    @Autowired
    public AsignacionService(AsignacionRepository asignacionRepository, CamionRepository camionRepository,
            RutaRepository rutaRepository) {
        this.asignacionRepository = asignacionRepository;
        this.camionRepository = camionRepository;
        this.rutaRepository = rutaRepository;
    }

    @Transactional(readOnly = true)
    public AsignacionDTO toDTO(Asignacion asignacion) {
        return new AsignacionDTO(
                asignacion.getId(),
                asignacion.getFechaAsignacion(),
                asignacion.getCamion().getId(),
                asignacion.getCamion().getMatricula(),
                asignacion.getCamion().getModelo(),
                asignacion.getRuta().getId(),
                asignacion.getRuta().getNombre(),
                asignacion.getRuta().getZona(),
                asignacion.getRuta().getDia_semana());
    }

    @Transactional(readOnly = true)
    public List<AsignacionDTO> toDTOList(List<Asignacion> asignaciones) {
        return asignaciones.stream().map(this::toDTO).toList();
    }

    // CREATE

    /*
     * Crear asignacion. No se crea la asignacion si:
     * - El camión no existe
     * - La ruta no existe
     */
    @Transactional
    public AsignacionDTO crearAsignacion(Long camionId, Long rutaId) {

        // Buscar el camión
        Optional<Camion> camionOpt = camionRepository.findById(camionId);
        if (camionOpt.isEmpty()) {
            throw new IllegalArgumentException("No se ha encontrado ningún camión con id: " + camionId);
        }

        // Buscar la ruta
        Optional<Ruta> rutaOpt = rutaRepository.findById(rutaId);
        if (rutaOpt.isEmpty()) {
            throw new IllegalArgumentException("No se ha encontrado ninguna ruta con id: " + rutaId);
        }

        // Comprobar si ya existe la asignación
        if (asignacionRepository.existsByCamionIdAndRutaId(camionId, rutaId)) {
            throw new IllegalArgumentException("Esta asignación ya existe");
        }

        // Crear la asignación
        Asignacion asignacion = new Asignacion();
        asignacion.setCamion(camionOpt.get());
        asignacion.setRuta(rutaOpt.get());

        return toDTO(asignacionRepository.save(asignacion));
    }

    // READ

    // Obtener todas las asignaciones
    @Transactional(readOnly = true)
    public List<AsignacionDTO> obtenerAsignaciones() {
        return toDTOList(asignacionRepository.findAll());
    }

    // Obtener asignacion por id
    @Transactional(readOnly = true)
    public AsignacionDTO obtenerAsignacionPorId(Long id) {
        Optional<Asignacion> asignacion = asignacionRepository.findById(id);
        if (asignacion.isPresent()) {
            return toDTO(asignacion.get());
        } else {
            throw new IllegalArgumentException("No se ha encontrado ninguna asignación con id: " + id);
        }
    }

    // DELETE físico

    // Eliminar asignacion de la BD
    @Transactional
    public void eliminarAsignacion(Long id) {
        if (!asignacionRepository.existsById(id))
            throw new IllegalStateException("No se ha encontrado ninguna asignación con id: " + id);
        asignacionRepository.deleteById(id);
    }

}
