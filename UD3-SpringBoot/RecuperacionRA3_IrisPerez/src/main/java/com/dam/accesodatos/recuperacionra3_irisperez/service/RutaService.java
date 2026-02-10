package com.dam.accesodatos.recuperacionra3_irisperez.service;

import com.dam.accesodatos.recuperacionra3_irisperez.DTO.*;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Camion;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Ruta;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Usuario;
import com.dam.accesodatos.recuperacionra3_irisperez.repository.RolRepository;
import com.dam.accesodatos.recuperacionra3_irisperez.repository.RutaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * SERVICIO: RutaService
 * 
 * Anotaciones utilizadas:
 * - @Service: Marca la clase como un componente de servicio de Spring.
 * - @Transactional: Gestiona automáticamente las transacciones.
 */
@Service
public class RutaService {

    private final RutaRepository rutaRepository;

    @Autowired
    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    @Autowired
    private RolRepository rolRepository;

    @Transactional(readOnly = true)
    public RutaDTO toDTO(Ruta ruta) {
        return new RutaDTO(
                ruta.getId(),
                ruta.getNombre(),
                ruta.getZona(),
                ruta.getDia_semana(),
                ruta.getHora_inicio(),
                ruta.getHora_fin(),
                ruta.getActiva(),
                ruta.getAsignaciones() != null ? ruta.getAsignaciones().size() : 0);
    }

    @Transactional(readOnly = true)
    public List<RutaDTO> toDTOList(List<Ruta> rutas) {
        return rutas.stream().map(this::toDTO).toList();
    }

    // CREATE

    /*
     * Crear ruta. No se crea el ruta si:
     * - Viene vacía
     */
    @Transactional
    public Ruta crearRuta(Ruta ruta) {

        if (ruta == null)
            throw new IllegalArgumentException("Ruta nula");

        return rutaRepository.save(ruta);
    }

    // READ

    // Obtener todos los rutas
    @Transactional(readOnly = true)
    public List<RutaDTO> obtenerRutas() {
        return toDTOList(rutaRepository.findAll());
    }

    // Obtener DTO ruta por id
    @Transactional(readOnly = true)
    public RutaDTO obtenerRutaDTOPorId(Long id) {
        Optional<Ruta> ruta = rutaRepository.findById(id);
        if (ruta.isPresent()) {
            return toDTO(ruta.get());
        } else {
            throw new IllegalArgumentException("No se ha encontrado ningún ruta con id: " + id);
        }
    }

    // Obtener ruta por id
    @Transactional(readOnly = true)
    public RutaDTO obtenerRutaPorId(Long id) {
        Optional<Ruta> ruta = rutaRepository.findById(id);
        if (ruta.isPresent()) {
            return toDTO(ruta.get());
        } else {
            throw new IllegalArgumentException("No se ha encontrado ningún ruta con id: " + id);
        }
    }

    // Obtener todas los rutas activas
    @Transactional(readOnly = true)
    public List<RutaDTO> obtenerRutasActivas() {
        return toDTOList(rutaRepository.findByActivaTrue());
    }

    // UPDATE

    // Actualizar ruta
    @Transactional
    public RutaDTO actualizarRuta(Long id, RutaUpdateDTO rutaActualizado) {
        if (rutaActualizado == null)
            throw new IllegalArgumentException("No se han recibido correctamente los nuevos datos.");

        Optional<Ruta> rutaAActualizar = rutaRepository.findById(id);

        if (rutaAActualizar.isEmpty()) {
            throw new IllegalStateException("El ruta no existe en la base de datos.");
        } else {
            Ruta ruta = rutaAActualizar.get();

            // Actualizo los campos (sobrescribo los originales con los nuevos)
            ruta.setNombre(rutaActualizado.getNombre());
            ruta.setZona(rutaActualizado.getZona());
            ruta.setDia_semana(rutaActualizado.getDia_semana());
            ruta.setHora_inicio(rutaActualizado.getHora_inicio());
            ruta.setHora_fin(rutaActualizado.getHora_fin());
            ruta.setActiva(rutaActualizado.getActiva());

            // Guardo la ruta en la base de datos y retorno los datos de la ruta actualizada
            return toDTO(rutaRepository.save(ruta));
        }
    }

    // DELETE lógico (Desactivar)

    // Metodo que cambia el estado de la ruta al contrario (activo -> inactivo, inactivo -> activo)
    public void interruptorEstado(Long id) {
        Ruta r = rutaRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("No se ha encontrado ningún ruta con id: " + id));
        r.setActiva(!r.getActiva()); // Cambia al estado contrario
        rutaRepository.save(r);
    }

    // DELETE físico

    // Eliminar ruta de la BD
    @Transactional
    public void eliminarRuta(Long id) {
        if (!rutaRepository.existsById(id))
            throw new IllegalStateException("No se ha encontrado ningún ruta con id: " + id);
        rutaRepository.deleteById(id);
    }

}
