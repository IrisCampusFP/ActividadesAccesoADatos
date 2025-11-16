package controller;

import dao.ProyectoDAO;
import model.Proyecto;
import service.TransaccionesService;
import view.Menus;
import view.VistaProyectos;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static utils.Utils.*;

public class ControladorProyectos {

    private final Menus m = new Menus();
    private final VistaProyectos vista = new VistaProyectos();
    private final ProyectoDAO proyectoDAO = new ProyectoDAO();
    private final TransaccionesService transacciones = new TransaccionesService();

    public void menuProyectos() {
        while (true) {
            m.menuProyectos();
            int opcion = leerOpcion();
            System.out.println();

            switch (opcion) {
                case 1:
                    crearProyecto();
                    break;
                case 2:
                    mostrarTodos();
                    break;
                case 3:
                    mostrarProyectoPorId();
                    break;
                case 4:
                    actualizarProyecto();
                    break;
                case 5:
                    eliminarProyecto();
                    break;
                case 6:
                    transferirPresupuesto();
                    break;
                case 0:
                    System.out.println("Volviendo al menú principal...");
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
            pausar();
        }
    }

    public int pedirIdProyecto() {
        int idProyecto = leerEntero();
        while (!proyectoDAO.existeProyecto(idProyecto)) {
            System.out.println("No hay ningún proyecto registrado con id '" + idProyecto + "'.");
            System.out.print("Introduce de nuevo el id del proyecto: ");
            return pedirIdProyecto();
        }
        return idProyecto;
    }

    public void transferirPresupuesto() {
        System.out.print("Introduce el id del proyecto de origen: ");
        int idProyectoOrigen = pedirIdProyecto();

        System.out.print("\nIntroduce el id del proyecto de destino: ");
        int idProyectoDestino = pedirIdProyecto();

        System.out.print("Introduce la cantidad que quieras transferir: ");
        BigDecimal monto = leerBigDecimal();

        // Si presupuesto del proyecto origen es menor al monto a transferir
        if (proyectoDAO.consultarPresupuesto(idProyectoOrigen).compareTo(monto) < 0) {
            System.out.println("No hay suficiente presupuesto en el proyecto " + idProyectoOrigen);
            return;
        }

        // Llamada al metodo de la transacción para realizar la transferencia
        try {
            boolean exito = transacciones.transferirPresupuesto(idProyectoOrigen, idProyectoDestino, monto);
            if (exito) {
                System.out.println("Transferencia realizada correctamente. Se han transferido " + monto + "€ del proyecto " + idProyectoOrigen + " al " + idProyectoDestino);
            } else {
                System.out.println("Error al realizar la transferencia de presupuesto.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ocurrió un error durante la transferencia.");
        }
    }

    private void crearProyecto() {
        Proyecto p = vista.pedirDatosProyecto();
        int idGenerado;
        if ((idGenerado = proyectoDAO.crear(p)) != -1) {
            System.out.println("Proyecto creado correctamente. (Id del proyecto: " + idGenerado + ")" );
        } else {
            System.out.println("No se ha creado el proyecto.");
        }
    }

    private void mostrarTodos() {
        // Se obtienen todos los proyectos de la base de datos
        List<Proyecto> listaProyectos = proyectoDAO.obtenerTodos();
        if (listaProyectos.isEmpty()) {
            System.out.println("No hay proyectos registrados.");
            return;
        }
        // Envío los proyectos a la vista para mostrarlos
        vista.mostrarProyectos(listaProyectos);
    }

    private void mostrarProyectoPorId() {
        int id = vista.solicitarIdProyecto();
        Optional proyecto = proyectoDAO.obtenerPorId(id);
        if (proyecto.isEmpty()) {
            System.out.println("Proyecto no encontrado.");
        } else {
            vista.mostrarDatosProyecto(proyecto);
        }
    }

    private void actualizarProyecto() {
        // Se solicita el id del proyecto a actualizar
        int id = vista.solicitarIdProyecto();
        // Se obtiene el proyecto a actualizar de la base de datos
        Optional<Proyecto> proyecto = proyectoDAO.obtenerPorId(id);

        // Si el proyecto existe, se piden al usuario los nuevos datos y se actualiza el registro en la base de datos
        if (proyecto.isPresent()) {
            Proyecto proyectoOriginal = proyecto.get();

            // Se muestran los datos del proyecto antes de actualizar
            System.out.println("DATOS ACTUALES DEL PROYECTO: ");
            vista.mostrarDatosProyecto(proyecto);

            String nombre = proyectoOriginal.getNombre();
            BigDecimal presupuesto = proyectoOriginal.getPresupuesto();
            LocalDate fechaInicio = proyectoOriginal.getFechaInicio();
            LocalDate fechaFin = proyectoOriginal.getFechaFin();

            int campo;
            do {
                // Se pregunta el campo a modificar
                vista.menuCampoAModificar();
                campo = leerOpcion();

                System.out.println();
                switch (campo) {
                    case 1:
                        nombre = vista.actualizarNombre();
                        break;
                    case 2:
                        presupuesto = vista.actualizarPresupuesto();
                        break;
                    case 3:
                        fechaInicio = vista.actualizarFechaInicio();
                        break;
                    case 4:
                        fechaFin = vista.actualizarFechaFin();
                        break;
                    default:
                        opcionInvalida();
                }
            } while (campo < 1 || campo > 4);

            // Se crea un nuevo objeto proyecto con los datos actualizados
            Proyecto proyectoActualizado = new Proyecto(id, nombre, presupuesto, fechaInicio, fechaFin);

            // Se actualiza el proyecto en la base de datos
            if (proyectoDAO.actualizar(proyectoActualizado)) {
                System.out.println("Proyecto actualizado correctamente.");
            } else {
                System.out.println("ERROR: No se ha actualizado el proyecto.");
            }
        } else {
            System.out.println("No existe ningún proyecto registrado con el id: " + id);
        }
    }

    private void eliminarProyecto() {
        int id = vista.solicitarIdProyecto();
        Optional proyecto = proyectoDAO.obtenerPorId(id);

        if (proyecto.isEmpty()) {
            System.out.println("Proyecto no encontrado.");
        } else {
            vista.mostrarDatosProyecto(proyecto);

            boolean confirmacion = vista.solicitarConfirmacion();
            if (confirmacion) {
                if (proyectoDAO.eliminar(id)) {
                    System.out.println("Proyecto " + id + " eliminado correctamente.");
                } else {
                    System.out.println("No se ha eliminado el proyecto.");
                }
            } else {
                System.out.println("Cancelando operación...");
            }
        }
    }
}
