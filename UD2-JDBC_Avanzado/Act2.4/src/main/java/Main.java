import conexion.ConexionPool;

public class Main {
    public static void main(String[] args) {

        EmpleadosDAO dao = new EmpleadosDAO();

        dao.listarEmpleados();

        ConexionPool.cerrarPool();
    }
}
