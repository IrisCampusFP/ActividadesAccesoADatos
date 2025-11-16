package view;

import model.Proyecto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static utils.Utils.*;

public class VistaProyectos {

    // Metodo para solicitar datos de un nuevo proyecto
    public Proyecto pedirDatosProyecto() {
        System.out.println("INTRODUCE LOS DATOS DEL PROYECTO");

        System.out.print("Nombre del proyecto: ");
        String nombre = leerString();

        System.out.print("Presupuesto: ");
        BigDecimal presupuesto = pedirPresupuesto();

        System.out.print("Fecha de inicio (Año-Mes-Día): ");
        LocalDate fechaInicio = leerFecha();

        System.out.print("Fecha de fin (Año-Mes-Día): ");
        LocalDate fechaFin = leerFecha();

        return new Proyecto(0, nombre, presupuesto, fechaInicio, fechaFin);
    }

    // Metodo para asegurar que el presupuesto no sea negativo
    public BigDecimal pedirPresupuesto() {
        BigDecimal presupuesto;
        do {
            presupuesto = leerBigDecimal();
            if (presupuesto.compareTo(BigDecimal.ZERO) < 0) {
                System.out.println("El presupuesto no puede ser negativo.");
                System.out.print("Vuelve a introducir el presupuesto: ");
            }
        } while (presupuesto.compareTo(BigDecimal.ZERO) < 0);
        return presupuesto;
    }

    // Metodo para solicitar un ID de proyecto
    public int solicitarIdProyecto() {
        System.out.print("Introduce el ID del proyecto: ");
        return leerEntero();
    }

    // Metodo para mostrar los datos de un proyecto
    public void mostrarDatosProyecto(Optional<Proyecto> p) {
        if (p.isPresent()) {
            Proyecto proyecto = p.get();
            System.out.printf("ID: %d | Nombre: %s | Presupuesto: %.2f | Fecha de Inicio: %s | Fecha de Fin: %s\n",
                    proyecto.getIdProyecto(), proyecto.getNombre(), proyecto.getPresupuesto(), proyecto.getFechaInicio(), proyecto.getFechaFin());
        } else {
            System.out.println("Error al mostrar los datos del proyecto.");
        }
    }

    // Metodo para mostrar la lista de proyectos
    public void mostrarProyectos(List<Proyecto> proyectos) {
        for (Proyecto p : proyectos) {
            mostrarDatosProyecto(Optional.of(p));
        }
    }

    // Metodo para solicitar la confirmación antes de eliminar un proyecto
    public boolean solicitarConfirmacion() {
        System.out.println("\n¿Estás segur@ de que quieres eliminar este proyecto?");
        return preguntaSiNo();
    }

    // Metodo para mostrar el menú de campos a modificar
    public void menuCampoAModificar() {
        System.out.println("\n¿Qué campo quieres modificar?");
        System.out.println("1. Nombre");
        System.out.println("2. Presupuesto");
        System.out.println("3. Fecha de inicio");
        System.out.println("4. Fecha de fin");
    }

    // Métodos para actualizar los campos del proyecto
    public String actualizarNombre() {
        System.out.print("Nuevo nombre: ");
        return leerString();
    }

    public BigDecimal actualizarPresupuesto() {
        System.out.print("Nuevo presupuesto: ");
        return pedirPresupuesto();
    }

    public LocalDate actualizarFechaInicio() {
        System.out.print("Nueva fecha de inicio (Año-Mes-Día): ");
        return leerFecha();
    }

    public LocalDate actualizarFechaFin() {
        System.out.print("Nueva fecha de fin (Año-Mes-Día): ");
        return leerFecha();
    }
}
