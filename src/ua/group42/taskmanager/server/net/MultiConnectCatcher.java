package ua.group42.taskmanager.server.net;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import ua.group42.taskmanager.server.ServerWrapperIface;

/**
 *
 * @author Silvan
 */
public class MultiConnectCatcher implements Runnable {

    private static final Logger log = Logger.getLogger(MultiConnectCatcher.class);
    private ServerWrapperIface server;
    private ServerSocket serverSocket;
    private boolean isStopped;
    private List<Runnable> workers = new LinkedList<Runnable>();
    // TODO: Thread pool

    @Override
    public void run() {
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException ex) {
                if (isStopped()) {
                    log.info("Server stopped");
                } else {
                    log.error("Error accepting client connection", ex);
                    throw new ConnectionException("Error accepting client connection", ex);
                }
            }
            if (clientSocket != null) {
                //Thread worker = new Thread(new ServWorker(server, clientSocket));
                Runnable worker = null;
                try {
                    worker = new ServWorker(server, clientSocket);
                } catch (IOException ex) {
                    System.exit(0);
                }
                workers.add(worker);
                worker.run(); // never be null;
            }
        }
    }

    public MultiConnectCatcher(ServerWrapperIface creator) {
        server = creator;
    }

    private boolean isStopped() {
        return isStopped;
    }
    
    public synchronized void disconnectUser(String login) {
            for (Runnable t : workers) {
                if (t != null && ((ServWorker) t).getLogin().equals(login)) {
                    ((ServWorker) t).stopService();
                    break;
                }
            }
    }

    public synchronized void stop() {
        isStopped = true;
        try {
            for (Runnable t : workers) {
                if (t != null) {
                    ((ServWorker) t).stopService();
                }
            }
            this.serverSocket.close();
        } catch (IOException ex) {
            log.error("Socket closing error", ex);
            throw new ConnectionException("Error closing server", ex);
        }
    }

    private void openServerSocket() {
        log.info("Opening server socket on " + server.getWorkPort() + " port. ");
        try {
            serverSocket = new ServerSocket(server.getWorkPort());
        } catch (IOException ex) {
            throw new ConnectionException("Cannot open port" + server.getWorkPort(), ex);
        }
    }
    
    public void secret() {
        for (Runnable r : workers) {
            ((ServWorker) r).update();
        }
    }
}
