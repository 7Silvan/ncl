package ua.group42.taskmanager.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 *
 * @author Silvan
 */
public class CustomObjectInputStream  {
    
    private ObjectInputStream ois;
    
    public CustomObjectInputStream() throws IOException{
    }
    
    public CustomObjectInputStream(InputStream in) throws IOException{
        ois = new ObjectInputStream(in);
    }
    
    public synchronized Object readObject() throws IOException, ClassNotFoundException {
        Object o;
        synchronized (ois) {
                o = ois.readObject();
        }
        return o;
    }
    
    public void close() throws IOException {
        ois.close();
    }    
}