
import java.sql.*;

import static conexion.ConexionBD.*;

public class DAO {
    Connection con;

    public DAO() throws Exception {
        con = getConexion();
    }

    // Listar empleados usando Statement
    public void listarEmpleados() {
        // try-with-resources: abre el Statement y el ResultSet; se cerrarán automáticamente
        try (Statement st = con.createStatement(); // crea un Statement para ejecutar consultas
             ResultSet rs = st.executeQuery("SELECT id, nombre, salario FROM empleados")) { // ejecuta la consulta y obtiene los resultados

            if (!rs.next()) {
                System.out.println("No hay empleados registrados.");
            } else {
                System.out.println("··· LISTA DE EMPLEADOS ···");

                // Itera sobre cada fila del ResultSet
                do {
                    // Imprime los valores de las columnas 'id', 'nombre' y 'salario' formateados
                    System.out.printf("ID: %d - Nombre: %s - Salario: %.2f%n",
                            rs.getInt("id"), // obtiene la columna 'id' como entero
                            rs.getString("nombre"), // obtiene la columna 'nombre' como cadena
                            rs.getDouble("salario")); // obtiene la columna 'salario' como double
                } while (rs.next());
            }

        } catch (Exception e) { // captura cualquier excepción (p. ej. SQLException)
            e.printStackTrace(); // imprime la traza de la excepción
        }
    }

    // Buscar empleados por id usando PreparedStatement
    public void buscarEmpleadoPorId(int id) {
        // Consulta parametrizada: selecciona todas las columnas de la tabla empleados
        // para el empleado cuyo id coincida con el parámetro '?'
        String sql = "SELECT * FROM empleados WHERE id = ?";

        // Prepara la sentencia SQL con parámetros para evitar inyección y mejorar rendimiento
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            // Asigna el valor id (id buscado por el usuario) al primer parámetro (índice 1) de la consulta
            ps.setInt(1, id);

            // Ejecuta la consulta y obtiene el conjunto de resultados
            // ResultSet en su propio try-with-resources
            try(ResultSet rs = ps.executeQuery()) {
                // Si hay una fila en el ResultSet, obtiene y muestra el nombre y salario
                if (rs.next()) {
                    System.out.println("··· DATOS DEL EMPLEADO CON ID " + id + " ···");
                    System.out.println("Empleado: " + rs.getString("nombre") + " - Salario: " + rs.getDouble("salario"));
                } else {
                    System.out.println("No se ha encontrado ningún empleado con id " + id);
                }
            } // rs se cierra aquí

            // Al finalizar el try-with-resources, la conexión se cerrará automáticamente.
            // El PreparedStatement y ResultSet deberían cerrarse explícitamente,
        } catch (Exception e) { // captura cualquier excepción (p. ej. SQLException u otras)
            e.printStackTrace(); // imprime la traza de la excepción en la salida de errores
        } // rs cerrado aquí
    }

    // Llamada a un procedimiento con CallableStatement
    public void obtenerEmpleadosConProcedimiento(int id) {
        // Preparación de la llamada al procedimiento almacenado.
        // La sintaxis "{call nombre_procedimiento(?)}" indica un procedimiento con 1 parámetro.
        try (CallableStatement cs = con.prepareCall("{call obtener_empleado(?)}")) {

            // Asignación de parámetros por índice (1-based).
            // Asigna el valor id (id buscado por el usuario) al primer parámetro (índice 1) de la consulta
            cs.setInt(1, id);

            // Ejecución de la llamada. Muchos procedimientos que realizan SELECTs devuelven un ResultSet.
            try (ResultSet rs = cs.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("No se ha encontrado ningún empleado con id " + id);
                } else {
                    System.out.println("··· DATOS DEL EMPLEADO CON ID " + id + " ···");

                    // Iteración sobre las filas devueltas. Se extraen columnas por nombre para mayor claridad.
                    do {
                        System.out.printf("ID: %d - Nombre: %s - Salario: %.2f%n",
                                rs.getInt("id"),      // obtiene 'id' como entero
                                rs.getString("nombre"), // obtiene 'nombre' como cadena
                                rs.getDouble("salario")); // obtiene 'salario' como double y lo formatea
                    } while (rs.next());
                }
            }

        } catch (SQLException e) {
            // Captura específica de SQLException: útil para depuración en ejemplos docentes.
            // En entornos reales se recomienda usar un logger estructurado y manejo por código SQLState.
            e.printStackTrace();
        } catch (Exception e) {
            // Captura genérica como medida de seguridad didáctica.
            e.printStackTrace();
        }
    }

    public void cerrarConexion() {
        cerrar();
    }
}
