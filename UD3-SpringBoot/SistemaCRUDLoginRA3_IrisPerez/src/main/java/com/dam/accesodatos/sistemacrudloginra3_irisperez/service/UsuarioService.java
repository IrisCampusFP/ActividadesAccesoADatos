package com.dam.accesodatos.sistemacrudloginra3_irisperez.service;

import com.dam.accesodatos.sistemacrudloginra3_irisperez.DTO.UsuarioDTO;
import com.dam.accesodatos.sistemacrudloginra3_irisperez.entity.Rol;
import com.dam.accesodatos.sistemacrudloginra3_irisperez.entity.Usuario;
import com.dam.accesodatos.sistemacrudloginra3_irisperez.repository.RolRepository;
import com.dam.accesodatos.sistemacrudloginra3_irisperez.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * SERVICIO: UsuarioService
 *
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

        int numMaxIntentosFallidos = 3;

        // Si el usuario está inactivo, no puede acceder
        if (!usuario.getActivo()) {
            throw new IllegalStateException("El usuario está inactivo");
        }

        // Comprobar contraseña
        boolean passwordCorrecta = usuario.checkPassword(password);

        if (passwordCorrecta) {
            usuario.setIntentosFallidos(0);
            usuario.setUltimoLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);
            return true;
        }

        // Si la contraseña no es correcta aumenta el numero de intentos fallidos
        int intentos = usuario.getIntentosFallidos() + 1;
        usuario.setIntentosFallidos(intentos);

        // Se guarda el numero de intentos fallidos
        usuarioRepository.save(usuario);

        // Si llega a 3 fallidos, el usuario se bloquea
        if (intentos >= numMaxIntentosFallidos) {
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
            throw new IllegalStateException("Usuario bloqueado. Has superado el número máximo de intentos fallidos (" + numMaxIntentosFallidos + ")");
        }
        return false;
    }


    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("No existe ningún usuario con el email indicado"));
    }


    // CREATE

    /* Crear usuario. No se crea el usuario si:
     * - Viene vacío
     * - Ya existe un usuario con ese username
     * - Ya existe un usuario con ese email
     * - Ya existe un usuario con ese dni
     */
    @Transactional
    public Usuario crearUsuario(Usuario usuario) {

        if (usuario == null) throw new IllegalArgumentException("Usuario nulo");

        comprobarUsernameUnico(usuario.getUsername());
        comprobarEmailUnico(usuario.getEmail());
        comprobarDniUnico(usuario.getDni());

        usuario.setPasswordHash(usuario.getPassword()); // Se hashea la contraseña del usuario
        usuario.setDni(usuario.getDni().toUpperCase()); // Se pasa la letra del DNI a mayúscula

        return usuarioRepository.save(usuario);
    }

    // READ

    // Obtener todos los usuarios
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    // Obtener usuario por id
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Obtener usuario por username
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("No se ha encontrado ningún usuario con el username '" + username + "'"));
    }

    // Obtener todos los usuarios activos
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    // UPDATE

    /**
     * Cambia la contraseña de un usuario verificando la contraseña actual.
     *
     * No se cambia la contraseña si:
     * - El usuario no existe
     * - La contraseña actual no es correcta
     * - La nueva contraseña se guarda hasheada (BCrypt)
     */
    @Transactional
    public void cambiarPassword(Long id, String passwordActual, String passwordNueva) {

        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se ha encontrado ningún usuario con id: " + id));

        if (!usuario.getActivo()) {
            throw new IllegalStateException("El usuario está desactivado");
        }

        if (!usuario.checkPassword(passwordActual)) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        if (passwordNueva == null || passwordNueva.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        usuario.setPasswordHash(passwordNueva);

        usuarioRepository.save(usuario);
    }

    /* Actualizar usuario. No se actualiza si:
     * - El usuario con los nuevos datos viene vacío
     * - El usuario a actualizar no existe en la base de datos
     */
    @Transactional
    public Usuario actualizarUsuario(Usuario usuario) {

        if(usuario == null) throw new IllegalArgumentException("No se han recibido correctamente los nuevos datos");

        Optional<Usuario> usuarioAActualizar = usuarioRepository.findById(usuario.getId());

        if(usuarioAActualizar.isEmpty()) throw new IllegalStateException("El usuario no existe en la base de datos");

        comprobarUsernameUnicoEditar(usuario.getUsername(), usuarioAActualizar.get().getUsername());
        comprobarEmailUnicoEditar(usuario.getEmail(), usuarioAActualizar.get().getEmail());
        comprobarDniUnicoEditar(usuario.getDni(), usuarioAActualizar.get().getDni());

        usuario.setDni(usuario.getDni().toUpperCase()); // Se pasa la letra del DNI a mayúscula

        return usuarioRepository.save(usuario);
    }

    // Recibe un id de usuario y una lista con ids de roles,
    // asigna los roles al usuario
    @Transactional
    public void actualizarRolesUsuario(Long idUsuario, List<Long> idsRoles) {

        // Obtengo el usuario por su id
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        if (idsRoles == null || idsRoles.isEmpty()) {
            throw new IllegalArgumentException("Un usuario debe tener al menos un rol");
        }

        // Creo un HashSet con los roles seleccionados
        Set<Rol> roles = new HashSet<>(rolRepository.findAllById(idsRoles));

        usuario.getRoles().clear(); // Limpio los roles originales
        usuario.setRoles(roles); // Establezco los nuevos roles

        usuarioRepository.save(usuario);
    }


    // DELETE lógico (Desactivar)

    /* NOTA: Al pasar el proyecto a web se ha sustituido este metodo por el de 'interruptorEstado' */

//    // Desactivar usuario (borrado lógico).
//    @Transactional
//    public Usuario desactivarUsuario(Long id) {
//
//        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new IllegalStateException("No se ha encontrado ningún usuario con id: " + id));
//
//        if(usuario.getActivo()){
//            usuario.setActivo(false);
//        } else throw new IllegalStateException("El usuario ya está inactivo");
//
//        return usuarioRepository.save(usuario);
//    }


    // Metodo que cambia el estado del usuario al contrario (activo -> inactivo, inactivo -> activo)
    public void interruptorEstado(Long id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(() -> new IllegalStateException("No se ha encontrado ningún usuario con id: " + id));
        if (u.getActivo()) {
            u.setActivo(false);
        } else  {
            u.setActivo(true);
            u.setIntentosFallidos(0); // Se resetean los intentos fallidos al reactivar el usuario
        }
        usuarioRepository.save(u);
    }

    // DELETE físico

    // Eliminar usuario de la BD
    @Transactional
    public void eliminarUsuario(Long id) {
        if(!usuarioRepository.existsById(id)) throw new IllegalStateException("No se ha encontrado ningún usuario con id: " + id);
        usuarioRepository.deleteById(id);
    }


    // CONTROL DE ACCESO A ENDPOINTS

    // Comprueba si el usuario es administrador (antes de acceder a un endpoint)
    public void comprobarAdmin(HttpSession session) {

        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuarioDTO");

        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado.");
        }

        if (!usuario.getRoles().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase("admin")))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
    }


    // COMPROBACIONES CAMPOS ÚNICOS

    public void comprobarUsernameUnico(String username) {
        if(usuarioRepository.existsByUsername(username)) throw new IllegalArgumentException("Ya existe un usuario con ese username");
    }

    public void comprobarEmailUnico(String email) {
        if(usuarioRepository.existsByEmail(email)) throw new IllegalArgumentException("Ya existe un usuario con ese email");
    }

    public void comprobarDniUnico(String dni) {
        if (usuarioRepository.existsByDni(dni)) throw new IllegalArgumentException("Ya existe un usuario con ese dni");
    }

    public void comprobarUsernameUnicoEditar(String username, String usernameUsuarioEditado) {
        if(usuarioRepository.existsByUsername(username) && !(username.equalsIgnoreCase(usernameUsuarioEditado))) throw new IllegalArgumentException("Ya existe un usuario con ese username");
    }

    public void comprobarEmailUnicoEditar(String email, String emailUsuarioEditado) {
        if(usuarioRepository.existsByEmail(email) && !(email.equalsIgnoreCase(emailUsuarioEditado))) throw new IllegalArgumentException("Ya existe un usuario con ese email");
    }

    public void comprobarDniUnicoEditar(String dni, String dniUsuarioEditado) {
        if (usuarioRepository.existsByDni(dni) && !(dni.equalsIgnoreCase(dniUsuarioEditado))) throw new IllegalArgumentException("Ya existe un usuario con ese dni");
    }

}
