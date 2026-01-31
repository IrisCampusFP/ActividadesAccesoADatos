package Actividad6;// Generos.java
import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Generos {
    
    @XmlElement(name = "genero")
    private List<String> genero;
    
    public Generos() {}
    
    public List<String> getGenero() { return genero; }
    public void setGenero(List<String> genero) { this.genero = genero; }
}