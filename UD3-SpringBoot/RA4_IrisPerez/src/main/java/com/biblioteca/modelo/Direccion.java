package com.biblioteca.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Direccion {
    private String calle;
    private String ciudad;

    @Column(name = "cp")
    private String codigoPostal;

    private String pais;

    // Constructor vacío
    public Direccion() {}

    public Direccion(String calle, String ciudad, String cp, String pais) {
        this.calle = calle;
        this.ciudad = ciudad;
        this.codigoPostal = cp;
        this.pais = pais;
    }

    // Getters y setters
    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String cp) { this.codigoPostal = cp; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
}