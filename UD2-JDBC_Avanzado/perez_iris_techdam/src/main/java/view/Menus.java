package view;

public class Menus {
    public void menuPrincipal() {
        System.out.println("\n······ MENÚ PRINCIPAL ······");
        System.out.println("1. Gestión de Empleados");
        System.out.println("2. Gestión de Proyectos");
        System.out.println("3. Asignar empleados a un proyecto [TRANSACCIÓN]");
        System.out.println("4. Mostrar tabla proyectos y empleados");
        System.out.println("0. Salir");
    }

    public void menuEmpleados() {
        System.out.println("\n··· GESTIÓN DE EMPLEADOS ···");
        System.out.println("1. Crear empleado");
        System.out.println("2. Mostrar todos los empleados");
        System.out.println("3. Mostrar empleado por id");
        System.out.println("4. Actualizar empleado");
        System.out.println("5. Eliminar empleado");
        System.out.println("6. [PROCEDIMIENTO] Actualizar el salario de todos los empleados de un departamento");
        System.out.println("7. [FUNCIÓN] Calcular la suma de todos los salarios de un departamento");
        System.out.println("0. Volver al menú principal");
    }

    public void menuProyectos() {
        System.out.println("\n··· GESTIÓN DE PROYECTOS ···");
        System.out.println("1. Crear proyecto");
        System.out.println("2. Mostrar todos los proyectos");
        System.out.println("3. Mostrar proyecto por id");
        System.out.println("4. Actualizar proyecto");
        System.out.println("5. Eliminar proyecto");
        System.out.println("6. Transferir presupuesto de un proyecto a otro [TRANSACCIÓN]");
        System.out.println("0. Volver al menú principal");
    }
}
