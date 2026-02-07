package com.dam.accesodatos.centromedicora3_irisperez.service;

import com.dam.accesodatos.centromedicora3_irisperez.DTO.PacienteDTO;
import com.dam.accesodatos.centromedicora3_irisperez.DTO.PacienteUpdateDTO;
import com.dam.accesodatos.centromedicora3_irisperez.DTO.UsuarioDTO;
import com.dam.accesodatos.centromedicora3_irisperez.DTO.UsuarioUpdateDTO;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Rol;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Usuario;
import com.dam.accesodatos.centromedicora3_irisperez.repository.PacienteRepository;
import com.dam.accesodatos.centromedicora3_irisperez.entity.Paciente;
import com.dam.accesodatos.centromedicora3_irisperez.repository.RolRepository;
import com.dam.accesodatos.centromedicora3_irisperez.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    @Autowired
    private UsuarioService usuarioService;

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

    @Transactional
    public PacienteDTO crearPacienteMedico(Paciente paciente, Long idMedico) {

        if (paciente == null) throw new IllegalArgumentException("Paciente nulo");

        Usuario medico = usuarioService.obtenerUsuarioPorId(idMedico);
        paciente.setMedico(medico);

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
        Usuario medico = usuarioService.obtenerUsuarioPorId(id);
        return toDTOList(pacienteRepository.findAllByMedico(medico));
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
    public PacienteDTO actualizarPaciente(Long id, PacienteUpdateDTO pacienteActualizado) {
        if(pacienteActualizado == null) throw new IllegalArgumentException("No se han recibido correctamente los nuevos datos");

        Optional<Paciente> pacienteAActualizar = pacienteRepository.findById(id);

        if (pacienteAActualizar.isEmpty()) {
            throw new IllegalStateException("El paciente no existe en la base de datos");
        } else {
            Paciente paciente = pacienteAActualizar.get();

            comprobarDniUnicoEditar(pacienteActualizado.getDni(), paciente.getDni());

            // Actualizo los campos (sobrescribo los originales con los nuevos)
            paciente.setNombre(pacienteActualizado.getNombre());
            paciente.setApellidos(pacienteActualizado.getApellidos());
            paciente.setDni(pacienteActualizado.getDni());
            paciente.setTelefono(pacienteActualizado.getTelefono());
            paciente.setFechaNacimiento(pacienteActualizado.getFechaNacimiento());
            paciente.setHistorial(pacienteActualizado.getHistorial());
            paciente.setActivo(pacienteActualizado.getActivo());

            // Guardo el paciente en la base de datos y retorno los datos del paciente actualizado
            return toDTO(pacienteRepository.save(paciente));
        }
    }

    // Recibe un id de paciente y un id de médico,
    // asigna el médico al paciente
    @Transactional
    public void actualizarMedicoPaciente(Long idPaciente, Long idMedico) {

        // Obtengo el paciente por su id
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new IllegalStateException("Paciente no encontrado."));

        if (idMedico == null) {
            throw new IllegalArgumentException("El paciente debe tener un médico asociado.");
        }

        // Obtengo el médico asociado al id recibido
        Usuario medico = usuarioService.obtenerUsuarioPorId(idMedico);

        // Asigno el médico al paciente
        paciente.setMedico(medico);

        pacienteRepository.save(paciente);
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

    public void comprobarDniUnicoEditar(String dniNuevo, String dniAnterior) {
        if (pacienteRepository.existsByDni(dniNuevo) && !(dniNuevo.equalsIgnoreCase(dniAnterior))) throw new IllegalArgumentException("Ya existe un paciente con ese dni");
    }

}
