import static utils.Utils.*;

public class Main {
    public static void main(String[] args) throws Exception {
        DAO dao = new DAO(); // Abro la conexión

        try {
            while (true) {
                System.out.println("\n·················· MENU ··················");
                System.out.println("1. Listar empleados (Statement)");
                System.out.println("2. Buscar empleado por id (PreparedStatement)");
                System.out.println("3. Obtener empleado por id (CallableStatement, usando un procedimiento)");
                System.out.println("0. SALIR");
                int opcion = leerOpcion();

                System.out.println();

                switch (opcion) {
                    case 1:
                        // Listar empleados usando Statement
                        dao.listarEmpleados();
                        break;
                    case 2:
                        // Buscar un empleado por su id usando PreparedStatement
                        System.out.print("Introduce el id del empleado que quieras buscar: ");
                        int id = leerEntero();
                        dao.buscarEmpleadoPorId(id);
                        break;
                    case 3:
                        // Llamada a un procedimiento con CallableStatement
                        System.out.print("Introduce el id del empleado que quieras buscar: ");
                        int idEmpleado = leerEntero();
                        dao.obtenerEmpleadosConProcedimiento(idEmpleado);
                        break;
                    case 0:
                        System.out.println("Cerrando programa...");
                        dao.cerrarConexion(); // Cierro la conexión
                        return;
                    default:
                        opcionInvalida();
                        break;
                }
                pausar();
            }
        } catch (Exception e) {
            System.out.println("Error al crear la conexión: " + e.getMessage());
        }
    }
}
