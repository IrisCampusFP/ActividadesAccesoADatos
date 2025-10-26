package nivel3Avanzado_sistemaReservasHotel.modelo;

public class Habitacion {
    private int numero;
    private String tipo; // Individual, Doble, Suite
    private double precioPorNoche;
    private boolean disponible;

    public Habitacion(int numero, String tipo, double precioPorNoche, boolean disponible) {
        this.numero = numero;
        this.tipo = tipo;
        this.precioPorNoche = precioPorNoche;
        this.disponible = disponible;
    }

    // Getters
    public int getNumero() {
        return numero;
    }
    public String getTipo() {
        return tipo;
    }
    public double getPrecioPorNoche() {
        return precioPorNoche;
    }
    public boolean getDisponible() {
        return disponible;
    }
}