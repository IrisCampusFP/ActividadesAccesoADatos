package controller;


import dao.AsignacionDAO;
import dao.EmpleadoDAO;
import dao.ProyectoDAO;
import model.Asignacion;
import model.Empleado;
import model.Proyecto;
import service.TransaccionesService;
import view.Menus;

import java.util.ArrayList;
import java.util.List;

import static utils.Utils.*;

public class ControladorPrincipal {

    private Menus m = new Menus();
    private ControladorEmpleados ce = new ControladorEmpleados();
    private ControladorProyectos cp = new ControladorProyectos();
    private TransaccionesService transacciones = new TransaccionesService();

    public void menuPrincipal() {
        while (true) {
            m.menuPrincipal();
            int opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    ce.menuEmpleados();
                    break;
                case 2:
                    cp.menuProyectos();
                    break;
                case 3:
                    asignarEmpleadosAProyecto();
                    break;
                case 4:
                    mostrarTablaCompleta();
                    break;
                case 0:
                    System.out.println("Cerrando programa... ¡Hasta pronto!");
                    sc.close();
                    return;
                default:
                    opcionInvalida();
            }
            pausar();
        }
    }

    public void asignarEmpleadosAProyecto() {
        // Se solicita el ID del proyecto
        System.out.print("Introduce el id del proyecto al que quieras agregar empleados: ");
        int idProyecto = leerEntero();

        // Se solicitan los ids de los empleados a agregar
        List<Integer> empleados = new ArrayList<>();
        boolean continuar = true;
        do {
            System.out.print("Introduce el id del empleado que quieras agregar al proyecto con id '" + idProyecto + "': ");
            int idEmpleado = leerEntero();
            empleados.add(idEmpleado);

            System.out.println("¿Quieres agregar otro empleado al proyecto " + idProyecto + "?");
            if (!preguntaSiNo()) {
                continuar = false;
            }
        } while (continuar);

        // Llama a la transacción para asignar los empleados al proyecto
        transacciones.asignarEmpleadosConSavepoint(idProyecto, empleados);
    }


    // METODO PARA MOSTRAR LA TABLA COMPLETA (Creado con ChatGPT-5)
    // MÉTODO PARA MOSTRAR LA TABLA COMPLETA (corregido)
    public void mostrarTablaCompleta() {
        AsignacionDAO asignacionDAO = new AsignacionDAO();
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        ProyectoDAO proyectoDAO = new ProyectoDAO();

        List<Asignacion> asignaciones = asignacionDAO.obtenerAsignaciones();

        // Ordenamos por proyecto y luego por empleado
        asignaciones.sort((a1, a2) -> {
            int cmp = Integer.compare(a1.getIdProyecto(), a2.getIdProyecto());
            if (cmp == 0) {
                Empleado e1 = empleadoDAO.obtenerPorId(a1.getIdEmpleado()).orElse(null);
                Empleado e2 = empleadoDAO.obtenerPorId(a2.getIdEmpleado()).orElse(null);
                if (e1 != null && e2 != null) {
                    return e1.getNombre().compareToIgnoreCase(e2.getNombre());
                }
            }
            return cmp;
        });

        // Utilidades locales (sin métodos extra)
        java.util.function.BiFunction<String, Integer, String> center = (txt, width) -> {
            String s = (txt == null || txt.isEmpty()) ? "-" : txt;
            if (s.length() >= width) return s.substring(0, width);
            int left = (width - s.length()) / 2;
            int right = width - s.length() - left;
            return " ".repeat(left) + s + " ".repeat(right);
        };
        java.text.DecimalFormat df = new java.text.DecimalFormat(
                "#,##0.00",
                new java.text.DecimalFormatSymbols(new java.util.Locale("es", "ES"))
        );
        java.util.function.Function<java.math.BigDecimal, String> euro = v -> v == null ? "-" : df.format(v) + " €";

        // Anchos de 12 columnas
        final int[] W = {5, 40, 17, 12, 12, 5, 25, 20, 15, 8, 5, 12};

        // Títulos de bloques
        final String T_PROY = "DATOS DEL PROYECTO";
        final String T_EMP  = "DATOS DEL EMPLEADO";
        final String T_ASIG = "DATOS DE ASIGNACIÓN";

        // Anchos de bloques (aseguramos que no se corten los títulos)
        int widthProyectoCalc   = W[0] + W[1] + W[2] + W[3] + W[4] + 4;   // 5 cols => 4 espacios
        int widthEmpleadoCalc   = W[5] + W[6] + W[7] + W[8] + W[9] + 4;   // 5 cols => 4 espacios
        int widthAsignacionCalc = W[10] + W[11] + 1;                      // 2 cols => 1 espacio

        int widthProyecto   = Math.max(widthProyectoCalc, T_PROY.length());
        int widthEmpleado   = Math.max(widthEmpleadoCalc, T_EMP.length());
        int widthAsignacion = Math.max(widthAsignacionCalc, T_ASIG.length());

        // Marco y líneas entre cabeceras principales
        String top = "┌" + "─".repeat(widthProyecto) + "┬" + "─".repeat(widthEmpleado) + "┬" + "─".repeat(widthAsignacion) + "┐";
        String mid = "├" + "─".repeat(widthProyecto) + "┼" + "─".repeat(widthEmpleado) + "┼" + "─".repeat(widthAsignacion) + "┤";

        System.out.println(top);
        System.out.println("│" + center.apply(T_PROY, widthProyecto)
                + "│" + center.apply(T_EMP, widthEmpleado)
                + "│" + center.apply(T_ASIG, widthAsignacion) + "│");
        System.out.println(mid);

        // Encabezado de columnas centrado
        String header = String.join(" ",
                center.apply("ID", W[0]),
                center.apply("Nombre Proyecto", W[1]),
                center.apply("Presupuesto", W[2]),
                center.apply("Fecha Inicio", W[3]),
                center.apply("Fecha Fin", W[4]),
                center.apply("ID", W[5]),
                center.apply("Nombre", W[6]),
                center.apply("Departamento", W[7]),
                center.apply("Salario", W[8]),
                center.apply("Activo", W[9]),
                center.apply("ID", W[10]),
                center.apply("Fecha", W[11])
        );
        System.out.println(header);
        System.out.println("-".repeat(header.length())); // línea separadora del ancho exacto del encabezado

        // Filas de datos
        for (Asignacion a : asignaciones) {
            Empleado emp = empleadoDAO.obtenerPorId(a.getIdEmpleado()).orElse(null);
            Proyecto proj = proyectoDAO.obtenerPorId(a.getIdProyecto()).orElse(null);
            if (emp == null || proj == null) continue;

            String fechaInicio = proj.getFechaInicio() != null ? proj.getFechaInicio().toString() : "-";
            String fechaFin = proj.getFechaFin() != null ? proj.getFechaFin().toString() : "-";
            String fechaAsignacion = a.getFecha() != null ? a.getFecha().toString() : "-";

            // Presupuesto y salario como BigDecimal
            java.math.BigDecimal presupuesto = proj.getPresupuesto();
            java.math.BigDecimal salario = emp.getSalario();

            String row = String.join(" ",
                    center.apply(String.valueOf(proj.getIdProyecto()), W[0]),
                    center.apply(proj.getNombre(), W[1]),
                    center.apply(euro.apply(presupuesto), W[2]),
                    center.apply(fechaInicio, W[3]),
                    center.apply(fechaFin, W[4]),
                    center.apply(String.valueOf(emp.getIdEmpleado()), W[5]),
                    center.apply(emp.getNombre(), W[6]),
                    center.apply(emp.getDepartamento(), W[7]),
                    center.apply(euro.apply(salario), W[8]),
                    center.apply(String.valueOf(emp.isActivo()), W[9]),
                    center.apply(String.valueOf(a.getIdAsignacion()), W[10]),
                    center.apply(fechaAsignacion, W[11])
            );
            System.out.println(row);
        }
    }
}