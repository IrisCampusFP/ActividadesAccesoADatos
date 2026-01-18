package com.dam.accesodatos.loginv2_irisperez.service;

import com.dam.accesodatos.loginv2_irisperez.entity.Usuario;
import com.dam.accesodatos.loginv2_irisperez.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * SERVICIO: UsuarioService
 * Clase de servicio que contiene la lógica de negocio
 * para la gestión de usuarios del sistema.
 *
 * Esta clase actúa como intermediaria entre la capa
 * de presentación (main / controlador) y el repositorio,
 * aplicando validaciones y reglas de negocio.
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

    @Transactional(readOnly = true)
    public boolean comprobarPassword(String password, String email) {

        Usuario usuario = obtenerUsuarioPorEmail(email);

        if (!usuario.getActivo()) {throw new IllegalStateException("El usuario está desactivado");}

        return usuario.checkPassword(password);
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

        comprobarUsername(usuario.getUsername());
        comprobarEmail(usuario.getEmail());
        comprobarDni(usuario.getDni());

        usuario.setPasswordHash(usuario.getPassword()); // Se hashea la contraseña del usuario
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

    /* Actualizar usuario. No se actualiza si:
     * - El usuario con los nuevos datos viene vacío
     * - El usuario a actualizar no existe en la base de datos
     */
    @Transactional
    public Usuario actualizarUsuario(Usuario usuario) {

        if(usuario == null) throw new IllegalArgumentException("No se han recibido correctamente los nuevos datos");

        Optional<Usuario> usuarioAActualizar = usuarioRepository.findById(usuario.getId());

        if(usuarioAActualizar.isEmpty()) throw new IllegalStateException("El usuario no existe en la base de datos");

        return usuarioRepository.save(usuario);
    }

    // DELETE lógico (Desactivar)

    // Desactivar usuario (borrado lógico).
    @Transactional
    public Usuario desactivarUsuario(Long id) {

        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new IllegalStateException("No se ha encontrado ningún usuario con id: " + id));

        if(usuario.getActivo()){
            usuario.setActivo(false);
        } else throw new IllegalStateException("El usuario ya está inactivo");

        return usuarioRepository.save(usuario);
    }

    // DELETE físico

    // Eliminar usuario de la BD
    @Transactional
    public void eliminarUsuario(Long id) {
        if(!usuarioRepository.existsById(id)) throw new IllegalStateException("No se ha encontrado ningún usuario con id: " + id);
        usuarioRepository.deleteById(id);
    }


    // COMPROBACIONES

    public void comprobarUsername(String username) {
        if(usuarioRepository.existsByUsername(username)) throw new IllegalArgumentException("Ya existe un usuario con ese username");
    }

    public void comprobarEmail(String email) {
        if(usuarioRepository.existsByEmail(email)) throw new IllegalArgumentException("Ya existe un usuario con ese email");
    }

    public void comprobarDni(String dni) {
        if (usuarioRepository.existsByDni(dni)) throw new IllegalArgumentException("Ya existe un usuario con ese dni");
    }

}
