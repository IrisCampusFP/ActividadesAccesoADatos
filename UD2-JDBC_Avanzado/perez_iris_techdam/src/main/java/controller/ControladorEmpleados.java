package controller;

import dao.EmpleadoDAO;
import model.Empleado;
import service.FuncionesService;
import service.ProcedimientosService;
import view.Menus;
import view.VistaEmpleados;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static utils.Utils.*;

public class ControladorEmpleados {

    private final Menus m = new Menus();
    private final VistaEmpleados vista = new VistaEmpleados();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final FuncionesService funciones = new FuncionesService();
    private final ProcedimientosService procedimientos = new ProcedimientosService();

    public void menuEmpleados() {
        while (true) {
            m.menuEmpleados();
            int opcion = leerOpcion();
            System.out.println();

            switch (opcion) {
                case 1:
                    crearEmpleado();
                    break;
                case 2:
                    mostrarTodos();
                    break;
                case 3:
                    mostrarEmpleadoPorId();
                    break;
                case 4:
                    actualizarEmpleado();
                    break;
                case 5:
                    eliminarEmpleado();
                    break;
                case 6:
                    actualizarSalarios();
                    break;
                case 7:
                    sumaSalarios();
                    break;
                case 0:
                    System.out.println("Volviendo al menú principal...");
                    return;
                default:
                    opcionInvalida();
            }
            pausar();
        }
    }

    public void actualizarSalarios() {
        String departamento = vista.pedirDepartamento();
        if (departamento != null) {
            System.out.print("Introduce un porcentaje actualizar los salarios del departamento de '" + departamento + "': ");
            double porcentaje = leerDouble();
            int empleadosActualizados = procedimientos.actualizarSalariosDepartamento(departamento, porcentaje);
            System.out.println("Se ha actualizado correctamente el salario de " + empleadosActualizados + " empleados (" + porcentaje + "%)");
        }
    }

    public void sumaSalarios() {
        String departamento = vista.pedirDepartamento();
        if (departamento != null) {
            funciones.calcularSumaSalarios(departamento);
        }
    }

    private void crearEmpleado() {
        Empleado e = vista.pedirDatosEmpleado();
        int idGenerado;
        if ((idGenerado = empleadoDAO.crear(e)) != -1) {
            System.out.println("Empleado creado correctamente. (Id del empleado: " + idGenerado + ")");
        } else {
            System.out.println("No se ha creado el empleado.");
        }
    }

    private void mostrarTodos() {
        List<Empleado> listaEmpleados = empleadoDAO.obtenerTodos();
        if (listaEmpleados.isEmpty()) {
            System.out.println("No hay empleados registrados.");
            return;
        }
        vista.mostrarEmpleados(listaEmpleados);
    }

    private void mostrarEmpleadoPorId() {
        int id = vista.solicitarIdEmpleado();
        Optional<Empleado> empleado = empleadoDAO.obtenerPorId(id);
        if (empleado.isEmpty()) {
            System.out.println("Empleado no encontrado.");
        } else {
            vista.mostrarDatosEmpleado(empleado.get());
        }
    }

    private void actualizarEmpleado() {
        int id = vista.solicitarIdEmpleado();
        Optional<Empleado> empleado = empleadoDAO.obtenerPorId(id);

        if (empleado.isPresent()) {
            Empleado empleadoOriginal = empleado.get();

            System.out.println("DATOS ACTUALES DEL EMPLEADO: ");
            vista.mostrarDatosEmpleado(empleado.get());

            String nombre = empleadoOriginal.getNombre();
            String departamento = empleadoOriginal.getDepartamento();
            BigDecimal salario = empleadoOriginal.getSalario();
            boolean estado = empleadoOriginal.isActivo();

            int campo;
            do {
                vista.menuCampoAModificar();
                campo = leerOpcion();

                System.out.println();
                switch (campo) {
                    case 1:
                        nombre = vista.actualizarNombre();
                        break;
                    case 2:
                        departamento = vista.actualizarDepartamento();
                        break;
                    case 3:
                        salario = vista.actualizarSalario();
                        break;
                    case 4:
                        estado = vista.pedirEstadoEmpleado();
                        ProcedimientosService procedimientosService = new ProcedimientosService();
                        // Llamo al procedimiento para actualizar el estado
                        if (procedimientosService.actualizarEstadoEmpleado(empleadoOriginal.getIdEmpleado(), estado)) {
                            System.out.println("Estado del empleado actualizado correctamente.");
                        } else {
                            System.out.println("Error al actualizar el estado del empleado.");
                        }
                        return;
                    default:
                        opcionInvalida();
                }
            } while (campo < 1 || campo > 4);

            Empleado empleadoActualizado = new Empleado(id, nombre, departamento, salario, estado);

            if (empleadoDAO.actualizar(empleadoActualizado)) {
                System.out.println("Empleado actualizado correctamente.");
            } else {
                System.out.println("ERROR: No se ha actualizado el empleado.");
            }
        } else {
            System.out.println("No existe ningún empleado registrado con el id: " + id);
        }
    }

    private void eliminarEmpleado() {
        int id = vista.solicitarIdEmpleado();
        Optional<Empleado> empleado = empleadoDAO.obtenerPorId(id);

        if (empleado.isEmpty()) {
            System.out.println("Empleado no encontrado.");
        } else {
            vista.mostrarDatosEmpleado(empleado.get());

            boolean confirmacion = vista.solicitarConfirmacion();
            if (confirmacion) {
                if (empleadoDAO.eliminar(id)) {
                    System.out.println("Empleado " + id + " eliminado correctamente.");
                } else {
                    System.out.println("No se ha eliminado el empleado.");
                }
            } else {
                System.out.println("Cancelando operación...");
            }
        }
    }
}
