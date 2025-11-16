import conexion.ConexionPool;

public class Main {
    public static void main(String[] args) {

        ProyectosDAO dao = new ProyectosDAO();

        if (dao.insertarProyecto("Sistema Solar Comunitario", 22000) &&
            dao.insertarProyecto("Red de Sensores Ambientales", 38000) &&
            dao.insertarProyecto("Plataforma Educativa STEM", 12500)) {

            System.out.println("Proyectos insertados correctamente.");

        } else {
            System.out.println("Error al insertar los proyectos");
        }


        if (dao.actualizarPresupuesto(1, 20000)) {
            System.out.println("Presupuesto actualizado correctamente.");
        } else {
            System.out.println("Error al actualizar el presupuesto.");
        }

        if (dao.eliminarProyecto(2)) {
            System.out.println("Proyecto eliminado correctamente.");
        } else {
            System.out.println("Error al eliminar el proyecto.");
        }

        dao.listarProyectos();

        ConexionPool.cerrarPool();
    }
}
