package ua.group42.taskmanager.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Silvan
 */
public class StopMonitor extends Thread {
    
    private ServerSocket serverSocket;
    private final String passFrase = "stop";
 
    public StopMonitor(int port) {
        setDaemon(true);
        setName("StopMonitor");
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
 
    @Override
    public void run() {
        System.out.println("stop monitor thread listening on: "+ serverSocket.getInetAddress()+":"+serverSocket.getLocalPort());
        Socket socket = null;
        BufferedReader reader;
        try {
            do {
                if (socket != null) socket.close();
                socket = serverSocket.accept();
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } while (!reader.readLine().equalsIgnoreCase(passFrase));
            System.out.println("stop signal received, stopping server");
            socket.close();
            serverSocket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }    
}
