package com.dam.accesodatos.recuperacionra3_irisperez.service;

import com.dam.accesodatos.recuperacionra3_irisperez.entity.Camion;
import com.dam.accesodatos.recuperacionra3_irisperez.repository.RolRepository;
import com.dam.accesodatos.recuperacionra3_irisperez.repository.CamionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * SERVICIO: CamionService

 * Anotaciones utilizadas:
 * - @Service: Marca la clase como un componente de servicio de Spring.
 * - @Transactional: Gestiona automáticamente las transacciones.
 */
@Service
public class CamionService {

    private final CamionRepository camionRepository;

    @Autowired
    public CamionService(CamionRepository camionRepository) {
        this.camionRepository = camionRepository;
    }

    @Autowired
    private RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<Camion> toDTOList(List<Camion> camiones) {
        return camiones;
    }

    // CREATE

    /* Crear camion. No se crea el camion si:
     * - Viene vacío
     * - Ya existe un camion con ese username
     * - Ya existe un camion con ese email
     */
    @Transactional
    public Camion crearCamion(Camion camion) {

        if (camion == null) throw new IllegalArgumentException("Camion nulo");


        return camionRepository.save(camion);
    }

    // READ

    // Obtener todos los camiones
    @Transactional(readOnly = true)
    public List<Camion> obtenerCamiones() {
        return camionRepository.findAll();
    }

    // Obtener DTO camion por id
    @Transactional(readOnly = true)
    public Camion obtenerCamionDTOPorId(Long id) {
        Optional<Camion> camion = camionRepository.findById(id);
        if (camion.isPresent()) {
            return camion.get();
        } else {
            throw new IllegalArgumentException("No se ha encontrado ningún camion con id: " + id);
        }
    }

    // Obtener camion por id
    @Transactional(readOnly = true)
    public Camion obtenerCamionPorId(Long id) {
        Optional<Camion> camion = camionRepository.findById(id);
        if (camion.isPresent()) {
            return camion.get();
        } else {
            throw new IllegalArgumentException("No se ha encontrado ningún camion con id: " + id);
        }
    }

    // Obtener todos los camiones activos
    @Transactional(readOnly = true)
    public List<Camion> obtenerCamionesActivos() {
        return camionRepository.findByActivoTrue();
    }


    // UPDATE

    /* Actualizar camion. No se actualiza si:
     * - El camion con los nuevos datos viene vacío
     * - El camion a actualizar no existe en la base de datos
     */
    @Transactional
    public Camion actualizarCamion(Long id, Camion camion) {
        if(camion == null) throw new IllegalArgumentException("No se han recibido correctamente los nuevos datos.");

        Optional<Camion> camionAActualizar = camionRepository.findById(id);

        if (camionAActualizar.isEmpty()) {
            throw new IllegalStateException("El camion no existe en la base de datos.");
        } else {
            return camionRepository.save(camion);
        }
    }


    // DELETE lógico (Desactivar)

    // Metodo que cambia el estado del camion al contrario (activo -> inactivo, inactivo -> activo)
    public void interruptorEstado(Long id) {
        Camion u = camionRepository.findById(id).orElseThrow(() -> new IllegalStateException("No se ha encontrado ningún camion con id: " + id));
        u.setActivo(!u.getActivo()); // Cambia al estado contrario
        camionRepository.save(u);
    }

    // DELETE físico

    // Eliminar camion de la BD
    @Transactional
    public void eliminarCamion(Long id) {
        if(!camionRepository.existsById(id)) throw new IllegalStateException("No se ha encontrado ningún camion con id: " + id);
        camionRepository.deleteById(id);
    }

}
