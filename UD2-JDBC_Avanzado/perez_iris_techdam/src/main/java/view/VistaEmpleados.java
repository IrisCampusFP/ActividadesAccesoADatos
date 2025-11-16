package view;

import model.Empleado;

import java.math.BigDecimal;
import java.util.List;

import static dao.EmpleadoDAO.existeDepartamento;
import static utils.Utils.*;

public class VistaEmpleados {

    // Método para solicitar datos de un nuevo empleado
    public Empleado pedirDatosEmpleado() {
        System.out.println("INTRODUCE LOS DATOS DEL EMPLEADO");

        System.out.print("Nombre del empleado: ");
        String nombre = leerString();

        String departamento = pedirDepartamento();
        while (departamento == null) {
            System.out.println("¿Quieres crear un nuevo departamento?");
            if (preguntaSiNo()) {
                System.out.print("Introduce el nombre del nuevo departamento: ");
                departamento = leerString();
            } else departamento = pedirDepartamento();
        }

        System.out.print("Salario: ");
        BigDecimal salario = pedirSalario();

        boolean estado = pedirEstadoEmpleado();

        return new Empleado(0, nombre, departamento, salario, estado);
    }

    // Metodo para asegurar que el salario no sea negativo
    public BigDecimal pedirSalario() {
        BigDecimal salario;
        do {
            salario = leerBigDecimal();
            if (salario.compareTo(BigDecimal.ZERO) < 0) {
                System.out.println("El salario no puede ser negativo.");
                System.out.print("Vuelve a introducir el salario: ");
            }
        } while (salario.compareTo(BigDecimal.ZERO) < 0);
        return salario;
    }

    // Método para solicitar un ID de empleado
    public int solicitarIdEmpleado() {
        System.out.print("Introduce el ID del empleado: ");
        return leerEntero();
    }

    // Método para mostrar los datos de un empleado
    public void mostrarDatosEmpleado(Empleado e) {
        System.out.printf("ID: %d | Nombre: %s | Departamento: %s | Salario: %.2f | Activo: %b\n",
                e.getIdEmpleado(), e.getNombre(), e.getDepartamento(), e.getSalario(), e.isActivo());
    }

    // Método para mostrar la lista de empleados
    public void mostrarEmpleados(List<Empleado> empleados) {
        for (Empleado e : empleados) {
            mostrarDatosEmpleado(e);
        }
    }

    // Método para solicitar confirmación antes de eliminar un empleado
    public boolean solicitarConfirmacion() {
        System.out.println("\n¿Estás segur@ de que quieres eliminar este empleado?");
        return preguntaSiNo();
    }

    // Método para mostrar el menú de campos a modificar
    public void menuCampoAModificar() {
        System.out.println("\n¿Qué campo quieres modificar?");
        System.out.println("1. Nombre");
        System.out.println("2. Departamento");
        System.out.println("3. Salario");
        System.out.println("4. Estado (Activo/Inactivo) [TRANSACCIÓN]");
    }

    // Métodos para actualizar los campos del empleado
    public String actualizarNombre() {
        System.out.print("Nuevo nombre: ");
        return leerString();
    }

    public String actualizarDepartamento() {
        String departamento = pedirDepartamento();
        while (departamento == null) {
            System.out.println("¿Quieres crear un nuevo departamento?");
            if (preguntaSiNo()) {
                System.out.print("Introduce el nombre del nuevo departamento: ");
                departamento = leerString();
            } else departamento = pedirDepartamento();
        }
        return departamento;
    }

    public BigDecimal actualizarSalario() {
        System.out.print("Nuevo salario: ");
        return pedirSalario();
    }

    public boolean pedirEstadoEmpleado() {
        System.out.println("Estado del empleado:\n1. Activo\n2. Inactivo");
        int opcion = leerOpcion();
        if (opcion == 1) {
            return true;
        } else if (opcion == 2) {
            return false;
        } else {
            System.out.println("Opción no válida. Por favor, introduce 1 para Activo, 2 para Inactivo.");
            return pedirEstadoEmpleado();
        }
    }

    // Solicita un nombre de departamento y comprueba si existe en la base de datos
    public String pedirDepartamento() {
        // Solicitar el nombre del departamento al usuario
        System.out.print("Introduce el nombre del departamento: ");
        String departamento = leerString();

        // Comprobar si el departamento existe en la base de datos
        if (!existeDepartamento(departamento)) {
            System.out.println("No se ha encontrado el departamento '" + departamento + "' en la base de datos.");
            return null;
        }

        return departamento;
    }
}
