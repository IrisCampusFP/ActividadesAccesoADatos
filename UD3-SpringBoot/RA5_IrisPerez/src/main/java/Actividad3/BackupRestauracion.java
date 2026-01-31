package Actividad3;

import Actividad2.ExistRESTClient;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class BackupRestauracion {
    
    private ExistRESTClient client;
    
    public BackupRestauracion(String host, int port, 
            String user, String pass) {
        this.client = new ExistRESTClient(host, port, user, pass);
    }
    
    public void backup(String colPath, String dirLocal) 
            throws Exception {
        
        Files.createDirectories(Path.of(dirLocal));
        
        String[] docs = AlmacenDocumentos.listarDocumentos(colPath);
        
        for (String doc : docs) {
            String contenido = client.get(colPath + "/" + doc);
            Files.writeString(
                Path.of(dirLocal, doc), contenido);
            System.out.println("Backup: " + doc);
        }
    }
    
    public void restaurar(String dirLocal, String colPath) 
            throws Exception {
        
        File dir = new File(dirLocal);
        for (File f : dir.listFiles((d, n) -> n.endsWith(".xml"))) {
            String contenido = Files.readString(f.toPath());
            client.put(colPath + "/" + f.getName(), contenido);
            System.out.println("Restaurado: " + f.getName());
        }
    }
}