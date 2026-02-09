package com.dam.accesodatos.recuperacionra3_irisperez.service;

import com.dam.accesodatos.recuperacionra3_irisperez.DTO.UsuarioDTO;
import com.dam.accesodatos.recuperacionra3_irisperez.DTO.UsuarioUpdateDTO;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Rol;
import com.dam.accesodatos.recuperacionra3_irisperez.entity.Usuario;
import com.dam.accesodatos.recuperacionra3_irisperez.repository.RolRepository;
import com.dam.accesodatos.recuperacionra3_irisperez.repository.UsuarioRepository;
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
 * SERVICIO: UsuarioService

 * Anotaciones utilizadas:
 * - @Service: Marca la clase como un componente de servicio de Spring.
 * - @Transactional: Gestiona automáticamente las transacciones.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Autowired
    private RolRepository rolRepository;

    @Transactional
    public boolean comprobarPassword(String password, String email) {

        Usuario usuario = obtenerUsuarioPorEmail(email);

        // Si el usuario está inactivo, no puede acceder
        if (!usuario.getActivo()) {
            throw new IllegalStateException("El usuario está inactivo");
        }

        // Comprobar contraseña
        if (usuario.checkPassword(password)) {
            usuarioRepository.save(usuario);
            return true;
        }

        return false;
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("No existe ningún usuario registrado con ese email."));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) return null;

        return new UsuarioDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getActivo(),
                usuario.getFechaCreacion(),
                usuario.getRoles()
        );
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> toDTOList(List<Usuario> usuarios) {
        return usuarios.stream().map(this::toDTO).toList();
    }

    // CREATE

    /* Crear usuario. No se crea el usuario si:
     * - Viene vacío
     * - Ya existe un usuario con ese username
     * - Ya existe un usuario con ese email
     */
    @Transactional
    public UsuarioDTO crearUsuario(Usuario usuario) {

        if (usuario == null) throw new IllegalArgumentException("Usuario nulo");

        comprobarUsernameUnico(usuario.getUsername());
        comprobarEmailUnico(usuario.getEmail());

        usuario.setPasswordHash(usuario.getPassword()); // Se hashea la contraseña del usuario

        return toDTO(usuarioRepository.save(usuario));
    }

    // READ

    // Obtener todos los usuarios
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerUsuarios() {
        return toDTOList(usuarioRepository.findAll());
    }

    // Obtener DTO usuario por id
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioDTOPorId(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            return toDTO(usuario.get());
        } else {
            throw new IllegalArgumentException("No se ha encontrado ningún usuario con id: " + id);
        }
    }

    // Obtener usuario por id
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            return usuario.get();
        } else {
            throw new IllegalArgumentException("No se ha encontrado ningún usuario con id: " + id);
        }
    }

    // Obtener usuario por username
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No se ha encontrado ningún usuario con el username '" + username + "'"));
        return toDTO(usuario);
    }

    // Obtener todos los usuarios activos
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerUsuariosActivos() {
        return toDTOList(usuarioRepository.findByActivoTrue());
    }

    // Obtener todos los médicos (usuarios con rol MEDICO)
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerUsuariosMedico() {
        return toDTOList(usuarioRepository.obtenerUsuariosMedico());
    }

    // UPDATE

    /**
     * Cambia la contraseña de un usuario verificando la contraseña actual.

     * No se cambia la contraseña si:
     * - El usuario no existe
     * - La contraseña actual no es correcta
     * - La nueva contraseña se guarda hasheada (BCrypt)
     */
    @Transactional
    public void cambiarPassword(Long id, String passwordActual, String passwordNueva) {

        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se ha encontrado ningún usuario con id: " + id));

        if (!usuario.getActivo()) {
            throw new IllegalStateException("El usuario está desactivado.");
        }

        if (!usuario.checkPassword(passwordActual)) {
            throw new IllegalArgumentException("Contraseña actual incorrecta.");
        }

        if (passwordNueva == null || passwordNueva.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }

        usuario.setPasswordHash(passwordNueva);

        usuarioRepository.save(usuario);
    }

    /* Actualizar usuario. No se actualiza si:
     * - El usuario con los nuevos datos viene vacío
     * - El usuario a actualizar no existe en la base de datos
     */
    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioUpdateDTO usuarioActualizado) {
        if(usuarioActualizado == null) throw new IllegalArgumentException("No se han recibido correctamente los nuevos datos.");

        Optional<Usuario> usuarioAActualizar = usuarioRepository.findById(id);

        if (usuarioAActualizar.isEmpty()) {
            throw new IllegalStateException("El usuario no existe en la base de datos.");
        } else {
            Usuario usuario = usuarioAActualizar.get();

            comprobarUsernameUnicoEditar(usuarioActualizado.getUsername(), usuario.getUsername());
            comprobarEmailUnicoEditar(usuarioActualizado.getEmail(), usuario.getEmail());

            // Actualizo los campos (sobrescribo los originales con los nuevos)
            usuario.setUsername(usuarioActualizado.getUsername());
            usuario.setEmail(usuarioActualizado.getEmail());
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setActivo(usuarioActualizado.getActivo());

            // Guardo el usuario en la base de datos y retorno los datos del usuario actualizado
            return toDTO(usuarioRepository.save(usuario));
        }
    }

    // Recibe un id de usuario y una lista con ids de roles,
    // asigna los roles al usuario
    @Transactional
    public void actualizarRolesUsuario(Long idUsuario, List<Long> idsRoles) {

        // Obtengo el usuario por su id
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado."));

        if (idsRoles == null || idsRoles.isEmpty()) {
            throw new IllegalArgumentException("Un usuario debe tener al menos un rol.");
        }

        // Creo un HashSet con los roles seleccionados
        Set<Rol> roles = new HashSet<>(rolRepository.findAllById(idsRoles));

        usuario.getRoles().clear(); // Limpio los roles originales
        usuario.setRoles(roles); // Establezco los nuevos roles

        usuarioRepository.save(usuario);
    }


    // DELETE lógico (Desactivar)

    // Metodo que cambia el estado del usuario al contrario (activo -> inactivo, inactivo -> activo)
    public void interruptorEstado(Long id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(() -> new IllegalStateException("No se ha encontrado ningún usuario con id: " + id));
        u.setActivo(!u.getActivo()); // Cambia al estado contrario
        usuarioRepository.save(u);
    }

    // DELETE físico

    // Eliminar usuario de la BD
    @Transactional
    public void eliminarUsuario(Long id) {
        if(!usuarioRepository.existsById(id)) throw new IllegalStateException("No se ha encontrado ningún usuario con id: " + id);
        usuarioRepository.deleteById(id);
    }


    // CONTROL DE PERMISOS

    // Comprueba que el usuario esté registrado y sea ADMIN
    public void comprobarAdmin(HttpSession session) {

        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuarioDTO");

        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado.");
        }

        if (usuario.getRoles().stream().noneMatch(r -> r.getNombre().equalsIgnoreCase("ADMIN")))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
    }

    // Comprueba que el usuario esté registrado y sea COORDINADOR
    public void comprobarCoordinador(HttpSession session) {

        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuarioDTO");

        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado.");
        }

        if (usuario.getRoles().stream().noneMatch(r -> r.getNombre().equalsIgnoreCase("COORDINADOR")))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
    }


    // COMPROBACIONES CAMPOS ÚNICOS

    public void comprobarUsernameUnico(String username) {
        if(usuarioRepository.existsByUsername(username)) throw new IllegalArgumentException("Ya existe un usuario con ese username.");
    }

    public void comprobarEmailUnico(String email) {
        if(usuarioRepository.existsByEmail(email)) throw new IllegalArgumentException("Ya existe un usuario con ese email.");
    }

    public void comprobarUsernameUnicoEditar(String usernameNuevo, String usernameAnterior) {
        if(usuarioRepository.existsByUsername(usernameNuevo) && !(usernameNuevo.equalsIgnoreCase(usernameAnterior))) throw new IllegalArgumentException("Ya existe un usuario con ese username.");
    }

    public void comprobarEmailUnicoEditar(String emailNuevo, String emailAnterior) {
        if(usuarioRepository.existsByEmail(emailNuevo) && !(emailNuevo.equalsIgnoreCase(emailAnterior))) throw new IllegalArgumentException("Ya existe un usuario con ese email.");
    }

}
