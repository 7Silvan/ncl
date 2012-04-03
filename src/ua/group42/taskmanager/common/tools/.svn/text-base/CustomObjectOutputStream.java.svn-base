package ua.group42.taskmanager.tools;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 *
 * @author Silvan
 */
public class CustomObjectOutputStream {
     private ObjectOutputStream oos;
    
    public CustomObjectOutputStream() throws IOException{
    }
    
    public CustomObjectOutputStream(OutputStream out) throws IOException{
        oos = new ObjectOutputStream(out);
    }
    
    public synchronized void writeObject(Object o) throws IOException {
        synchronized (oos) {
            oos.writeObject(o);
        }
    }
    
    public void close() throws IOException {
        oos.close();
    }
}
