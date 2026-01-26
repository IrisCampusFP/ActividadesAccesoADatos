package com.biblioteca.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Representa un producto en el sistema de inventario.
 * * <p>Los productos tienen un código único, información básica
 * (nombre, precio, stock) y un estado que indica si están activos.</p>
 * * <h2>Ejemplo de uso:</h2>
 * <pre>{@code
 * Producto p = new Producto(1, "Laptop", new BigDecimal("999.99"), 10);
 * if (p.estaDisponible()) {
 * System.out.println("Valor stock: " + p.calcularValorStock());
 * }
 * }</pre>
 * * @author Rafael Medina
 * @version 1.0
 * @since 2025
 * @see EstadoProducto
 */
@Entity
@Table(name = "productos")
public class Producto {

    /**
     * Identificador único del producto.
     * Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código de producto único para identificación interna.
     * Debe ser único en el sistema.
     */
    @Column(unique = true, nullable = false)
    private Integer codigo;

    /**
     * Nombre descriptivo del producto.
     * Máximo 100 caracteres.
     */
    @Column(length = 100, nullable = false)
    private String nombre;

    /**
     * Precio unitario del producto.
     * Precisión de 10 dígitos con 2 decimales.
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    /**
     * Cantidad de unidades disponibles en inventario.
     * No puede ser negativo (se inicializa en 0).
     */
    @Column(nullable = false)
    private Integer stock = 0;

    /**
     * Estado del producto en el sistema.
     * Por defecto se inicializa como ACTIVO.
     * @see EstadoProducto
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProducto estado = EstadoProducto.ACTIVO;

    // -----------------------------------------------------------------
    // CONSTRUCTORES
    // -----------------------------------------------------------------

    /**
     * Constructor vacío requerido por JPA.
     */
    public Producto() {}

    /**
     * Constructor para crear un producto con sus datos principales.
     * * @param codigo Código único del producto.
     * @param nombre Nombre descriptivo.
     * @param precio Precio unitario.
     * @param stock Cantidad inicial.
     */
    public Producto(Integer codigo, String nombre, BigDecimal precio, Integer stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.estado = EstadoProducto.ACTIVO;
    }

    // -----------------------------------------------------------------
    // LÓGICA DE NEGOCIO
    // -----------------------------------------------------------------

    /**
     * Verifica si el producto está disponible para venta.
     * * <p>Un producto está disponible si tiene stock mayor a cero
     * y su estado es ACTIVO.</p>
     * * @return {@code true} si el producto está disponible,
     * {@code false} en caso contrario
     */
    public boolean estaDisponible() {
        return stock != null && stock > 0 && estado == EstadoProducto.ACTIVO;
    }

    /**
     * Calcula el valor total del stock disponible.
     * * <p>El valor se calcula multiplicando el precio unitario
     * por la cantidad de unidades en stock.</p>
     * * @return valor total del stock como {@link BigDecimal}
     * @throws IllegalStateException si precio o stock son null
     */
    public BigDecimal calcularValorStock() {
        if (precio == null || stock == null) {
            throw new IllegalStateException(
                    "Precio y stock son requeridos para calcular valor");
        }
        return precio.multiply(BigDecimal.valueOf(stock));
    }

    // -----------------------------------------------------------------
    // GETTERS Y SETTERS
    // -----------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public EstadoProducto getEstado() { return estado; }
    public void setEstado(EstadoProducto estado) { this.estado = estado; }
}