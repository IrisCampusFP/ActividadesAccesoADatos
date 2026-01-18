package com.dam.accesodatos.loginv2_irisperez.view;

import com.dam.accesodatos.loginv2_irisperez.entity.Usuario;

import java.util.List;

import static com.dam.accesodatos.loginv2_irisperez.utils.LoginUtils.*;


public class UsuarioView {

    public void mostrarMenu() {
        System.out.println("\nMENÚ GESTIÓN USUARIOS:");
        System.out.println("1. Test de conexión");
        System.out.println("2. Crear usuario");
        System.out.println("3. Listar usuarios");
        System.out.println("4. Buscar por username");
        System.out.println("5. Actualizar usuario");
        System.out.println("6. Desactivar usuario (borrado lógico)");
        System.out.println("7. Eliminar usuario (borrado físico)");
        System.out.println("0. Salir");
    }

    public String pedirNombre() {
        System.out.print("Nombre: ");
        return leerString();
    }

    public String pedirApellidos() {
        System.out.print("Apellidos: ");
        return leerString();
    }

    public String pedirUsername() {
        System.out.print("Nombre de usuario: ");
        return sc.nextLine();
    }

    public String pedirEmail() {
        System.out.print("Email: ");
        return leerEmail();
    }

    public String pedirPassword() {
        System.out.print("Contraseña: ");
        return sc.nextLine();
    }

    public String pedirDni() {
        System.out.print("DNI: ");
        return leerDNI();
    }

    public Long solicitarIdUsuario() {
        System.out.print("Introduce el ID del usuario: ");
        return leerLong();
    }

    public String solicitarUsername() {
        System.out.print("Introduce el username: ");
        return leerString();
    }

    public void mostrarUsuario(Usuario usuario) {
        System.out.println(usuario);
    }

    public void mostrarUsuarios(List<Usuario> usuarios) {
        usuarios.forEach(this::mostrarUsuario);
    }

    public int pedirCampoAModificar() {
        System.out.println("\nCAMPO A MODIFICAR:");
        System.out.println("1. Nombre");
        System.out.println("2. Apellidos");
        System.out.println("3. Username");
        System.out.println("4. Email");
        System.out.println("5. Password");
        System.out.println("6. DNI");
        System.out.print("Elige una opción: ");
        return leerEntero();
    }
}