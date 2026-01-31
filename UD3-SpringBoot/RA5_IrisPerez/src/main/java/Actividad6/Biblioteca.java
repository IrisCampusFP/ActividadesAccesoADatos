package Actividad6;// Biblioteca.java
import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "biblioteca")
@XmlAccessorType(XmlAccessType.FIELD)
public class Biblioteca {
    
    @XmlElement(name = "libro")
    private List<Libro> libro;
    
    public Biblioteca() {}
    
    public List<Libro> getLibro() { return libro; }
    public void setLibro(List<Libro> libro) { this.libro = libro; }
}