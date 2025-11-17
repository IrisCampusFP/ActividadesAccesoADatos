package view;

public class Menus {
    public void menuPrincipal() {
        System.out.println("\n······ MENÚ PRINCIPAL ······");
        System.out.println("1. Gestión Libros (CRUD)");
        System.out.println("2. Ejecutar Transacción insertar género y lista de libros del género");
        System.out.println("3. Ejecutar procedimiento actualizar stock");
        System.out.println("0. Salir");
    }

    public void menuLibros() {
        System.out.println("\n··· GESTIÓN DE LIBROS ···");
        System.out.println("1. Crear libro");
        System.out.println("2. Mostrar todos los libros");
        System.out.println("3. Mostrar libro por id");
        System.out.println("4. Actualizar libro");
        System.out.println("5. Eliminar libro");
        System.out.println("0. Volver al menú principal");
    }
}
