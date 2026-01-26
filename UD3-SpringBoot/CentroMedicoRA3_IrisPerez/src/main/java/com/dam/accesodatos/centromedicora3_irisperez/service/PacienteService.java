package com.dam.accesodatos.centromedicora3_irisperez.service;

import com.dam.accesodatos.centromedicora3_irisperez.repository.PacienteRepository;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Paciente;
import com.dam.accesodatos.centromedicora3_irisperez.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * SERVICIO: PacienteService
 *
 * Anotaciones utilizadas:
 * - @Service: Marca la clase como un componente de servicio de Spring.
 * - @Transactional: Gestiona automáticamente las transacciones.
 */
@Service
public class PacienteService {

    @Autowired
    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Autowired
    private RolRepository rolRepository;

    // CREATE
    
    @Transactional
    public Paciente crearPaciente(Paciente paciente) {

        if (paciente == null) throw new IllegalArgumentException("Paciente nulo");

        comprobarDniUnico(paciente.getDni());

        paciente.setDni(paciente.getDni().toUpperCase()); // Se pasa la letra del DNI a mayúscula

        return pacienteRepository.save(paciente);
    }
    
    
    // READ

    // Obtener todos los pacientes
    @Transactional(readOnly = true)
    public List<Paciente> obtenerPacientes() {
        return pacienteRepository.findAll();
    }

    // Obtener paciente por id
    @Transactional(readOnly = true)
    public Optional<Paciente> obtenerPacientePorId(Long id) {
        return pacienteRepository.findById(id);
    }

    // Obtener todos los pacientes activos
    @Transactional(readOnly = true)
    public List<Paciente> obtenerPacientesActivos() {
        return pacienteRepository.findByActivoTrue();
    }

    // UPDATE

    /* Actualizar paciente. No se actualiza si:
     * - El paciente con los nuevos datos viene vacío
     * - El paciente a actualizar no existe en la base de datos
     */
    @Transactional
    public Paciente actualizarPaciente(Paciente paciente) {

        if(paciente == null) throw new IllegalArgumentException("No se han recibido correctamente los nuevos datos");

        Optional<Paciente> pacienteAActualizar = pacienteRepository.findById(paciente.getId());

        if(pacienteAActualizar.isEmpty()) throw new IllegalStateException("El paciente no existe en la base de datos");

        comprobarDniUnicoEditar(paciente.getDni(), pacienteAActualizar.get().getDni());

        paciente.setDni(paciente.getDni().toUpperCase()); // Se pasa la letra del DNI a mayúscula

        return pacienteRepository.save(paciente);
    }

    // DELETE lógico (Desactivar)

    // Metodo que cambia el estado del paciente al contrario (activo -> inactivo, inactivo -> activo)
    public void interruptorEstado(Long id) {
        Paciente u = pacienteRepository.findById(id).orElseThrow(() -> new IllegalStateException("No se ha encontrado ningún paciente con id: " + id));
        if (u.getActivo()) {
            u.setActivo(false);
        } else  {
            u.setActivo(true);
        }
        pacienteRepository.save(u);
    }

    // DELETE físico

    // Eliminar paciente de la BD
    @Transactional
    public void eliminarPaciente(Long id) {
        if(!pacienteRepository.existsById(id)) throw new IllegalStateException("No se ha encontrado ningún paciente con id: " + id);
        pacienteRepository.deleteById(id);
    }


    // COMPROBACIONES CAMPOS ÚNICOS

    public void comprobarDniUnico(String dni) {
        if (pacienteRepository.existsByDni(dni)) throw new IllegalArgumentException("Ya existe un paciente con ese dni");
    }

    public void comprobarDniUnicoEditar(String dni, String dniPacienteEditado) {
        if (pacienteRepository.existsByDni(dni) && !(dni.equalsIgnoreCase(dniPacienteEditado))) throw new IllegalArgumentException("Ya existe un paciente con ese dni");
    }

}
