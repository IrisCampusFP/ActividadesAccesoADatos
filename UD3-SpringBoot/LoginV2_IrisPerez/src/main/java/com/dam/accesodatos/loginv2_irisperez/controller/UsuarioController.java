package com.dam.accesodatos.loginv2_irisperez.controller;

import com.dam.accesodatos.loginv2_irisperez.entity.Usuario;
import com.dam.accesodatos.loginv2_irisperez.service.UsuarioService;
import com.dam.accesodatos.loginv2_irisperez.view.UsuarioView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.dam.accesodatos.loginv2_irisperez.utils.LoginUtils.*;


@Component
public class UsuarioController{

    @Autowired
    UsuarioService usuarioService;

    UsuarioView vista = new UsuarioView();

    public void iniciarSesion() {
        while (true) {
            System.out.println("INICIAR SESIÓN");
            System.out.print("Introduce tu email: ");
            String email = leerEmail();

            System.out.print("Introduce tu contraseña: ");
            String password = sc.nextLine();

            try {
                if (!usuarioService.comprobarPassword(password, email)) {
                    throw new IllegalStateException("Email o contraseña incorrectos");
                }

                Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);

                menuGestionUsuarios();
                return;
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    public void menuGestionUsuarios() {
        while (true) {
            vista.mostrarMenu();
            int opcion = leerOpcion();
            System.out.println();

            switch (opcion) {
                case 1 -> testConexion();
                case 2 -> crearUsuario();
                case 3 -> listarUsuarios();
                case 4 -> buscarUsuarioPorUsername();
                case 5 -> actualizarUsuario();
                case 6 -> desactivarUsuario();
                case 7 -> eliminarUsuario();
                case 0 -> {
                    System.out.println("Cerrando la aplicación. ¡Hasta pronto!");
                    System.exit(0);
                }
                default -> mensajeOpcionInvalida();
            }
            pausar();
        }
    }


    /*
     * Se realiza un test simple de conexión consultando el total de usuarios.
     * Si la consulta funciona, la conexión y JPA están operativos.
     */
    private void testConexion() {
        try {
            List<Usuario> usuarios = usuarioService.obtenerUsuarios();
            System.out.println("¡Conexión exitosa :)!");
            System.out.println("Número de usuarios registrados en la BD: " + usuarios.size());
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }


    private void crearUsuario() {

        System.out.println("CREACIÓN DE USUARIO");
        String nombre = vista.pedirNombre();
        String apellidos = vista.pedirApellidos();
        String username = pedirUsernameUnico();
        String email = pedirEmailUnico();
        String password = vista.pedirPassword();
        String dni = pedirDniUnico();

        Usuario usuario = new Usuario(nombre, apellidos, username, email, password, dni);

        try {
            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);
            System.out.println("Usuario creado correctamente :). Datos del usuario:" + usuarioCreado);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Error al crear usuario: " + e.getMessage());
        }
    }

    public String pedirUsernameUnico() {
        while (true) {
            try {
                String username = vista.pedirUsername();
                usuarioService.comprobarUsername(username);
                return username; // solo se devuelve si es válido y único
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public String pedirEmailUnico() {
        while (true) {
            try {
                String email = vista.pedirEmail();
                usuarioService.comprobarEmail(email);
                return email; // solo se devuelve si es válido y único
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public String pedirDniUnico() {
        while (true) {
            try {
                String dni = vista.pedirDni();
                usuarioService.comprobarDni(dni);
                return dni; // solo se devuelve si es válido y único
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    private void listarUsuarios() {
        try {
            List<Usuario> listaUsuarios = usuarioService.obtenerUsuarios();
            if (listaUsuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados.");
                return;
            }
            System.out.println("LISTA DE USUARIOS REGISTRADOS:");
            vista.mostrarUsuarios(listaUsuarios);
        } catch (IllegalStateException e){
            System.out.println(e.getMessage());
        }
    }

    private void buscarUsuarioPorUsername() {
        String username = vista.solicitarUsername();
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);
            vista.mostrarUsuario(usuario);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void actualizarUsuario() {

        Long id = vista.solicitarIdUsuario();

        try {
            // 1) Obtener el usuario de la BD
            Optional<Usuario> usuarioObtenido = usuarioService.obtenerUsuarioPorId(id);

            if (usuarioObtenido.isEmpty()){
                System.out.println("No se ha encontrado ningún usuario con id: " + id);
            } else {
                Usuario usuario = usuarioObtenido.get();

                // 2) Mostrar datos actuales
                System.out.println("DATOS ACTUALES DEL USUARIO:");
                vista.mostrarUsuario(usuario);

                // 3) Pedir qué campo modificar y el nuevo valor
                int campo = vista.pedirCampoAModificar();

                switch (campo) {
                    case 1:
                        usuario.setNombre(vista.pedirNombre());
                        break;
                    case 2:
                        usuario.setApellidos(vista.pedirApellidos());
                        break;
                    case 3:
                        String username = vista.pedirUsername();
                        usuarioService.comprobarUsername(username);
                        usuario.setUsername(username);
                        break;
                    case 4:
                        String email = vista.pedirEmail();
                        usuarioService.comprobarEmail(email);
                        usuario.setEmail(email);
                        break;
                    case 5:
                        usuario.setPasswordHash(vista.pedirPassword());
                        break;
                    case 6:
                        String dni = vista.pedirDni();
                        usuarioService.comprobarDni(dni);
                        usuario.setDni(dni);
                        break;
                    default:
                        mensajeOpcionInvalida();
                }

                // 4) Guardar cambios
                Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
                System.out.println("Usuario actualizado correctamente :). Datos actualizados:");
                vista.mostrarUsuario(usuarioActualizado);
            }
        } catch (Exception e) {
            System.out.println("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    private void desactivarUsuario() {
        try {
            System.out.println("DESACTIVAR USUARIO");
            Long id = vista.solicitarIdUsuario();
            Usuario usuarioDesactivado = usuarioService.desactivarUsuario(id);
            System.out.println("Usuario desactivado correctamente. Datos del usuario desactivado: " + usuarioDesactivado);
        } catch (Exception e) {
            System.out.println("Error al desactivar usuario: " + e.getMessage());
        }
    }

    private void eliminarUsuario() {
        try {
            System.out.println("ELIMINAR USUARIO");
            Long id = vista.solicitarIdUsuario();
            usuarioService.eliminarUsuario(id);
            System.out.println("Usuario eliminado correctamente.");
        } catch (Exception e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        }
    }
}