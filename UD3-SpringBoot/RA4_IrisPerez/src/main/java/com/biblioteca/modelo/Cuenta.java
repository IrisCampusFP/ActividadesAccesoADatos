package com.biblioteca.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cuentas")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Columna mágica: JPA incrementa esto automáticamente en cada UPDATE (v = v + 1)
    @Version
    private Long version;

    private String numero;

    @Column(precision = 15, scale = 2)
    private BigDecimal saldo;

    public Cuenta() {}

    // Constructor auxiliar
    public Cuenta(String numero, BigDecimal saldo) {
        this.numero = numero;
        this.saldo = saldo;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public Long getVersion() { return version; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
}