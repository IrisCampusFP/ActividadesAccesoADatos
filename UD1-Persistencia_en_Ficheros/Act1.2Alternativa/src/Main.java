import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        GestionBancaria gc = new GestionBancaria();
        try {
            gc.iniciarPrograma();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}