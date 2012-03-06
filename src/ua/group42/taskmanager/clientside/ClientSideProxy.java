package ua.group42.taskmanager.clientside;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import ua.group42.taskmanager.Listener;
import ua.group42.taskmanager.configuration.Config.ClientSets;
import ua.group42.taskmanager.configuration.ConfigReader;
import ua.group42.taskmanager.control.ControllerIface;
import ua.group42.taskmanager.control.InternalControllerException;
import ua.group42.taskmanager.model.InvalidTaskException;
import ua.group42.taskmanager.model.Task;
import ua.group42.taskmanager.protocol.NetProtocol;

/**
 *
 * @author Silvan
 */
public class ClientSideProxy implements ControllerIface {

    private static final Logger log = Logger.getLogger(ClientSideProxy.class);
        
    private ConfigReader config = null;
    private Socket connection = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;

    public ClientSideProxy(ConfigReader configReader) throws IOException {
        try {
            
            config = configReader;
            
            ClientSets clientSets = configReader.getClientSets();
            String host = clientSets.getHost();
            Integer port = clientSets.getPort();
            
            String login = JOptionPane.showInputDialog("Input login", "admin");
            //String login = clientSets.getLogin();
            
            connection = new Socket(InetAddress.getByName(host), port);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
            String logInRequest = NetProtocol.ClientSide.requestLogIn(login);
            writer.write(logInRequest);
            writer.flush();
            log.info("Request sent: " + logInRequest);
            
            
            
        } catch (IOException ex) {
            log.error("ClientSide Socket Connection Error", ex);
            throw ex; // TODO: write your own exeption;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                    log.info ("reader closed");
                }
                if (writer != null) {
                    writer.close();
                    log.info ("writer closed");
                }
                if (connection != null) {
                    connection.close();
                    log.info ("connection closed");
                }
            } catch (IOException ex) {
                log.error("Closing streams error", ex);
            }
        }
    }

    @Override
    public void addTask(Task task) throws InvalidTaskException, InternalControllerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addTask(String name, String description, String contacts, String date) throws InvalidTaskException, InternalControllerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addPostponedTask(Task task) throws InvalidTaskException, InternalControllerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTask(Task task) throws InternalControllerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void editTask(Task was, Task become) throws InvalidTaskException, InternalControllerException {
        removeTask(was);
        addTask(become);
    }

    @Override
    public Collection<Task> getTasks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addListener(Listener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListener(Listener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stopService() {
        try {
            writer.close();
            reader.close();
            //TODO: send stopService (Client) to server
            connection.close();
        } catch (IOException ex) {
            log.error("Closing connection input stream error", ex);
            //TODO: use your written exception (Runtime)
        }
    }

    @Override
    public String getDateFormat() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addTask(String name, String description, String date) throws InvalidTaskException, InternalControllerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTask(String id) throws InternalControllerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void editTask(String idTaskWas, Task become) throws InvalidTaskException, InternalControllerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void editTask(String id, String name, String description, String date) throws InvalidTaskException, InternalControllerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * this call is delegated one for getting configurator 
     * like from TC but at Client side
     * @return ConfigReader instance
     */
    @Override
    public ConfigReader getConfigReader() {
        return config;
    }
}
