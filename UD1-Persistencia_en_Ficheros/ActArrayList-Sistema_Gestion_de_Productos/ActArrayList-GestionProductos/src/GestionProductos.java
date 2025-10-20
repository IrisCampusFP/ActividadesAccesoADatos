import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;

public class GestionProductos {
    private ArrayList<Producto> inventario = new ArrayList<>();
    private static Scanner sc = new Scanner(System.in);

    public void agregarInventarioBase() {
        inventario.add(new Producto("P001", "Portátil", 899.99));
        inventario.add(new Producto("P002", "Ratón", 25.50));
        inventario.add(new Producto("P003", "Teclado", 45.00));
        inventario.add(new Producto("P004", "Monitor", 199.99));
        inventario.add(new Producto("P005", "Webcam", 59.90));
    }

    public void menuPrincipal() {
        int opcion;
        while (true) {
            System.out.println("\n··· MENÚ ···");
            System.out.println("1. Mostrar todos los productos.");
            System.out.println("2. Buscar un producto por código.");
            System.out.println("3. Buscar un producto por nombre (verificar si existe).");
            System.out.println("4. Mostrar cuantos productos hay en el inventario.");
            System.out.println("5. Cambiar el precio de un producto.");
            System.out.println("6. Eliminar un producto del inventario.");
            System.out.println("7. Añadir un nuevo producto.");
            System.out.println("8. Mostrar el precio total del inventario.");
            System.out.println("9. Mostrar el producto más caro.");
            System.out.println("10. Mostrar los productos con precio superior a 50€.");
            System.out.println("11. SALIR.");
            opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    mostrarProductos();
                    break;
                case 2:
                    System.out.print("Introduce el código del producto: ");
                    String codigo = sc.nextLine();

                    Producto p = buscarProductoPorCodigo(codigo);
                    if (p == null) {
                        System.out.println("No hay ningún producto en el inventario con el código: " + codigo);
                    } else {
                        System.out.println("Producto encontrado: " + p);}
                    break;
                case 3:
                    System.out.print("Introduce el nombre del producto: ");
                    String nombre = sc.nextLine();
                    mostrarProductoPorNombre(nombre);
                    break;
                case 4:
                    System.out.println("Hay " + inventario.size() + " productos en el inventario.");
                    break;
                case 5:
                    cambiarPrecioProducto();
                    break;
                case 6:
                    eliminarProducto();
                    break;
                case 7:
                    agregarProducto();
                    break;
                case 8:
                    System.out.println("Precio total del inventario: " + calcularPrecioTotal());
                    break;
                case 9:
                    Producto productoMasCaro = productoMasCaro();
                    if (productoMasCaro != null) {
                        System.out.println("Producto más caro del inventario: " + productoMasCaro);
                    } else {
                        System.out.println("No hay ningún producto en el inventario.");
                    }
                    break;
                case 10:
                    productosSuperioresA(50);
                    break;
                case 11:
                    System.out.println("Cerrando programa...");
                    sc.close();
                    return;
                default:
                    System.out.println("Opción no válida. Por favor, selecciona una opción entre 1 y 11.");
            }
            pausar();
        }
    }

    // Muestra todos los productos del Arraylist inventario
    public void mostrarProductos() {
        System.out.println("\nPRODUCTOS EN EL INVENTARIO:");
        if (inventario.isEmpty()) {
            System.out.println("No hay productos en el inventario.");
            return;
        }
        for (Producto p : inventario) {
            System.out.println(p);
        }
    }

    // Busca un producto por su codigo
    public Producto buscarProductoPorCodigo(String codigo) {
        for (Producto p : inventario) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null;
    }


    // Busca un producto por su nombre
    public Producto buscarProductoPorNombre(String nombre) {
        for (Producto p : inventario) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                return p;
            }
        }
        return null;
    }

    // Busca un producto por su nombre y, si existe, muestra sus datos
    public void mostrarProductoPorNombre(String nombre) {
        Producto p = buscarProductoPorNombre(nombre);
        if (p != null) {
            System.out.println("Existe un producto llamado '" + nombre + "' en el inventario.");
            System.out.println("Datos del producto: " + p);
        } else {
            System.out.println("No hay ningún producto llamado '" + nombre + "' en el inventario.");
        }
    }

    // Cambia el precio de un producto
    public void cambiarPrecioProducto() {
        System.out.print("Introduce el nombre del producto a modificar: ");
        String nombre = sc.nextLine();
        Producto p = buscarProductoPorNombre(nombre);
        if (p == null) {
            System.out.println("No hay ningún producto llamado '" + nombre + "' en el inventario.");
            return;
        }
        System.out.println("Producto encontrado: " + p);
        System.out.print("Introduce el nuevo precio del producto: ");
        double nuevoPrecio = leerPrecio();
        p.setPrecio(nuevoPrecio);
        System.out.println("Precio actualizado correctamente.");
        System.out.println("Datos del producto: " + p);
    }

    // Eliminar producto por código
    public void eliminarProducto() {
        System.out.print("Introduce el código del producto a eliminar: ");
        String codigo = sc.nextLine();
        // Elimino el producto utilizando un Iterator
        Iterator<Producto> it = inventario.iterator();
        boolean eliminado = false;
        while (it.hasNext()) {
            Producto p = it.next();
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                it.remove();
                eliminado = true;
                System.out.println("Producto eliminado del inventario (" + p + ").");
                break;
            }
        }
        if (!eliminado) {
            System.out.println("No se ha encontrado ningún producto con el código: " + codigo);
        }
    }

    // Añadir un producto nuevo
    public void agregarProducto() {
        System.out.print("Introduce el código del nuevo producto: ");
        String codigo = sc.nextLine();
        if (buscarProductoPorCodigo(codigo) != null) {
            System.out.println("ERROR: Ya existe un producto con ese código, introduce un código diferente.");
            return;
        }
        System.out.print("Introduce el nombre del producto: ");
        String nombre = sc.nextLine();
        System.out.print("Introduce el precio del producto: ");
        double precio = leerPrecio();
        if (precio < 0) {
            System.out.println("ERROR: El precio no puede ser negativo.");
            return;
        }
        Producto nuevo = new Producto(codigo, nombre, precio);
        inventario.add(nuevo);
        System.out.println("Producto añadido: " + nuevo);
    }

    // Calcular el precio total del inventario sumando el precio de todos los productos
    public double calcularPrecioTotal() {
        double precioTotal = 0;
        for (Producto p : inventario) {
            precioTotal += p.getPrecio();
        }
        return precioTotal;
    }

    // Encontrar el producto más caro
    public Producto productoMasCaro() {
        if (inventario.isEmpty()) return null;
        Producto pMasCaro = inventario.get(0); // Asumo que el producto más caro es el primer producto del inventario
        for (Producto p : inventario) {
            if (p.getPrecio() > pMasCaro.getPrecio()) { // Por cada producto, si es mas caro que el anterior
                pMasCaro = p;                           // lo establezco como el producto más caro.
            }
        }
        return pMasCaro; // Devuelvo como resultado el producto más caro de todos
    }

    // Devuelve un ArrayList de los productos con precio superior al valor recibido
    public void productosSuperioresA(double precioMinimo) {
        System.out.println("Productos con precio superior a " + precioMinimo + "€:");
        boolean hayProductos = false;
        for (Producto p : inventario) {
            if (p.getPrecio() > precioMinimo) {
                System.out.println(p);
                hayProductos = true;
            }
        }
        if (!hayProductos) {
            System.out.println("No hay ningún producto con precio superior a " + precioMinimo + "€.");
        }
    }


    // MÉTODOS DE CONTROL DE ENTRADA DE USUARIO:

    public int leerEntero() {
        try {
            int num = sc.nextInt();
            sc.nextLine(); // Limpiar buffer
            return num;
        } catch (InputMismatchException e) {
            sc.nextLine(); // Limpiar buffer
            System.out.print("Por favor, introduce un número entero: ");
            return leerEntero();
        }
    }

    public int leerOpcion() {
        System.out.print("↳ Seleccione una opción: ");
        return leerEntero();
    }

    public double leerDouble() {
        try {
            double d = sc.nextDouble();
            sc.nextLine(); // limpiar buffer
            return d;
        } catch (InputMismatchException e) {
            sc.nextLine(); // limpiar buffer
            System.out.print("Por favor, introduce un número: ");
            return leerDouble();
        }
    }

    public double leerPrecio() {
        double precio = leerDouble();
        while (precio < 0) {
            System.out.println("ERROR: El precio no puede ser negativo.");
            System.out.print("Por favor, introduce un número positivo: ");
            precio = leerDouble();
        }
        return precio;
    }

    public static void pausar() {
        System.out.print("\nPresiona ENTER para continuar...");
        sc.nextLine();
    }
}