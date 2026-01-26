package com.biblioteca.modelo;


import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("REVISTA")
public class Revista extends Recurso {

    @Column(length = 20)
    private String issn;

    private Integer numero;

    @Column(length = 20)
    private String mes;

    public Revista() {}

    public String getIssn() { return issn; }
    public void setIssn(String issn) { this.issn = issn; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }
}