package com.dam.accesodatos.recuperacionra3_irisperez.service;

import com.dam.accesodatos.recuperacionra3_irisperez.entity.Ruta;
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
    public List<Ruta> toDTOList(List<Ruta> rutaes) {
        return rutaes;
    }

    // CREATE

    /* Crear ruta. No se crea el ruta si:
     * - Viene vacío
     * - Ya existe un ruta con ese username
     * - Ya existe un ruta con ese email
     */
    @Transactional
    public Ruta crearRuta(Ruta ruta) {

        if (ruta == null) throw new IllegalArgumentException("Ruta nula");

        return rutaRepository.save(ruta);
    }

    // READ

    // Obtener todos los rutaes
    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutas() {
        return rutaRepository.findAll();
    }

    // Obtener DTO ruta por id
    @Transactional(readOnly = true)
    public Ruta obtenerRutaDTOPorId(Long id) {
        Optional<Ruta> ruta = rutaRepository.findById(id);
        if (ruta.isPresent()) {
            return ruta.get();
        } else {
            throw new IllegalArgumentException("No se ha encontrado ningún ruta con id: " + id);
        }
    }

    // Obtener ruta por id
    @Transactional(readOnly = true)
    public Ruta obtenerRutaPorId(Long id) {
        Optional<Ruta> ruta = rutaRepository.findById(id);
        if (ruta.isPresent()) {
            return ruta.get();
        } else {
            throw new IllegalArgumentException("No se ha encontrado ningún ruta con id: " + id);
        }
    }

    // Obtener todas los rutas activas
    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutasActivas() {
        return rutaRepository.findByActivaTrue();
    }

    // UPDATE

    /* Actualizar ruta. No se actualiza si:
     * - El ruta con los nuevos datos viene vacío
     * - El ruta a actualizar no existe en la base de datos
     */
    @Transactional
    public Ruta actualizarRuta(Long id, Ruta ruta) {
        if(ruta == null) throw new IllegalArgumentException("No se han recibido correctamente los nuevos datos.");

        Optional<Ruta> rutaAActualizar = rutaRepository.findById(id);

        if (rutaAActualizar.isEmpty()) {
            throw new IllegalStateException("El ruta no existe en la base de datos.");
        } else {
            return rutaRepository.save(ruta);
        }
    }


    // DELETE lógico (Desactivar)

    // Metodo que cambia el estado de la ruta al contrario (activo -> inactivo, inactivo -> activo)
    public void interruptorEstado(Long id) {
        Ruta r = rutaRepository.findById(id).orElseThrow(() -> new IllegalStateException("No se ha encontrado ningún ruta con id: " + id));
        r.setActiva(!r.getActiva()); // Cambia al estado contrario
        rutaRepository.save(r);
    }

    // DELETE físico

    // Eliminar ruta de la BD
    @Transactional
    public void eliminarRuta(Long id) {
        if(!rutaRepository.existsById(id)) throw new IllegalStateException("No se ha encontrado ningún ruta con id: " + id);
        rutaRepository.deleteById(id);
    }

}
