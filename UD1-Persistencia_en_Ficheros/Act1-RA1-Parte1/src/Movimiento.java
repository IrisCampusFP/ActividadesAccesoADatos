public class Movimiento {
    String tipo;
    double cantidad;
    Cliente cliente;

    public Movimiento(String tipo, double cantidad, Cliente cliente) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "Movimiento{" +
                "tipo='" + tipo + '\'' +
                ", cantidad=" + cantidad +
                ", cliente=" + cliente +
                '}';
    }
}
