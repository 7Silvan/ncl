package ua.group42.taskmanager.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author Silvan
 */
public class MultiConnectCatcher  implements Runnable {
    
    private static final Logger log = Logger.getLogger(MultiConnectCatcher.class);
    private ServerSideWrapperIface server;
    
    private ServerSocket serverSocket;
    private boolean isStopped;

    @Override
    public void run() {
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException ex) {
                if (isStopped())
                    log.info("Server stopped");
                else
                {
                    log.error("Error accepting client connection", ex);
                    throw new RuntimeException("Error accepting client connection", ex);
                }
            }
            if (clientSocket != null)
                new Thread(new ServWorker(server, clientSocket)).start();
        }
    }
    
    public MultiConnectCatcher (ServerSideWrapperIface creator) {
        server = creator;
    }
    
    private synchronized boolean isStopped() {
        return isStopped;
    }
    
    public synchronized void stop(){
        isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException ex) {
            log.error("Socket closing error",ex);
            throw new RuntimeException("Error closing server", ex);
        }
    }
    
    private void openServerSocket() {
        log.info("Opening server socket ...");
        try {
            serverSocket = new ServerSocket(server.getWorkPort());
        } catch (IOException ex) {
            throw new RuntimeException("Cannot open port" + server.getWorkPort(), ex);
        }
    }
}
