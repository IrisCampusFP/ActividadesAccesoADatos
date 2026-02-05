package com.dam.accesodatos.centromedicora3_irisperez.service;

import com.dam.accesodatos.centromedicora3_irisperez.DTO.PacienteDTO;
import com.dam.accesodatos.centromedicora3_irisperez.DTO.UsuarioDTO;
import com.dam.accesodatos.centromedicora3_irisperez.DTO.UsuarioUpdateDTO;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Usuario;
import com.dam.accesodatos.centromedicora3_irisperez.repository.PacienteRepository;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Paciente;
import com.dam.accesodatos.centromedicora3_irisperez.repository.RolRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    @Transactional(readOnly = true)
    public PacienteDTO toDTO(Paciente paciente) {
        if (paciente == null) return null;

        return new PacienteDTO(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellidos(),
                paciente.getDni(),
                paciente.getTelefono(),
                paciente.getFechaNacimiento(),
                paciente.getHistorial(),
                paciente.getMedico(),
                paciente.getActivo(),
                paciente.getFechaCreacion()
        );
    }

    @Transactional(readOnly = true)
    public List<PacienteDTO> toDTOList(List<Paciente> pacientes) {
        return pacientes.stream().map(this::toDTO).toList();
    }

    // CREATE

    @Transactional
    public PacienteDTO crearPaciente(Paciente paciente) {

        if (paciente == null) throw new IllegalArgumentException("Paciente nulo");

        comprobarDniUnico(paciente.getDni());

        paciente.setDni(paciente.getDni().toUpperCase()); // Se pasa la letra del DNI a mayúscula

        return toDTO(pacienteRepository.save(paciente));
    }


    // READ

    // Obtener todos los pacientes
    @Transactional(readOnly = true)
    public List<PacienteDTO> obtenerPacientes() {
        return toDTOList(pacienteRepository.findAll());
    }

    // Obtener los pacientes de un médico por su id
    @Transactional(readOnly = true)
    public List<PacienteDTO> obtenerPacientesMedico(Long id) {
        return toDTOList(pacienteRepository.findAllByMedico_Id(id));
    }

    // Obtener paciente por id
    @Transactional(readOnly = true)
    public PacienteDTO obtenerPacientePorId(Long id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        if (paciente.isPresent()) {
            return toDTO(paciente.get());
        } else {
            throw new IllegalArgumentException("No se ha encontrado ningún paciente con id: " + id);
        }
    }

    // Obtener todos los pacientes activos
    @Transactional(readOnly = true)
    public List<PacienteDTO> obtenerPacientesActivos() {
        return toDTOList(pacienteRepository.findByActivoTrue());
    }

    // UPDATE

    @Transactional
    public PacienteDTO actualizarPaciente(Long id, PacienteDTO pacienteActualizado) {
        if(pacienteActualizado == null) throw new IllegalArgumentException("No se han recibido correctamente los nuevos datos");

        Optional<Paciente> pacienteAActualizar = pacienteRepository.findById(id);

        if (pacienteAActualizar.isEmpty()) {
            throw new IllegalStateException("El paciente no existe en la base de datos");
        } else {
            Paciente paciente = pacienteAActualizar.get();

            comprobarDniUnicoEditar(paciente.getDni(), pacienteActualizado.getDni());

            // Actualizo los campos (sobrescribo los originales con los nuevos)
            paciente.setNombre(pacienteActualizado.getNombre());
            paciente.setApellidos(pacienteActualizado.getApellidos());
            paciente.setDni(pacienteActualizado.getDni());
            paciente.setTelefono(pacienteActualizado.getTelefono());
            paciente.setFechaNacimiento(pacienteActualizado.getFechaNacimiento());
            paciente.setHistorial(pacienteActualizado.getHistorial());
            paciente.setMedico(pacienteActualizado.getMedico());
            paciente.setActivo(pacienteActualizado.getActivo());

            // Guardo el paciente en la base de datos y retorno los datos del paciente actualizado
            return toDTO(pacienteRepository.save(paciente));
        }
    }

    // DELETE lógico (Desactivar)

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
