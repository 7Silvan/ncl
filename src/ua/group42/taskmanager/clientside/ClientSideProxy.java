package ua.group42.taskmanager.clientside;

import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import ua.group42.taskmanager.Listener;
import ua.group42.taskmanager.Notifiable;
import ua.group42.taskmanager.Updatable;
import ua.group42.taskmanager.configuration.Config.ClientSets;
import ua.group42.taskmanager.configuration.ConfigReader;
import ua.group42.taskmanager.control.ControllerIface;
import ua.group42.taskmanager.control.InternalControllerException;
import ua.group42.taskmanager.model.InvalidTaskException;
import ua.group42.taskmanager.model.Task;
import ua.group42.taskmanager.protocol.MessageParser;
import ua.group42.taskmanager.protocol.NetProtocol;
import ua.group42.taskmanager.protocol.RequestParser;
import ua.group42.taskmanager.protocol.ResponseParser;
import ua.group42.taskmanager.tools.Messenger;

/**
 *
 * @author Silvan
 */
public final class ClientSideProxy implements Runnable, ControllerIface {

    private static final Logger log = Logger.getLogger(ClientSideProxy.class);
    private ConfigReader config = null;
    private Socket connection = null;
    private Messenger messenger = null;
    private Boolean stop = false; // signal for stopping service
    private String login = null;
    private List<Listener> listeners = new LinkedList<Listener>();
    private InputStream inputStream;

    private ResponseParser logIn(String login) throws IOException {
        String request = NetProtocol.ClientSide.requestLogIn(login);
        log.debug("trying to lock messenger by logIn");
        messenger.checkOwners();
        synchronized (messenger) {
            messenger.owned();
            log.debug("messenger locked  by logIn!");
            messenger.write(request);
            log.info("LOGIN Request sent: \n" + request);
            messenger.leaving();
        }
            return messenger.getNextResponse();
    }

    private ResponseParser logOut(String login) throws IOException {
        String request = NetProtocol.ClientSide.requestLogOut(login);
        log.debug("trying to lock messenger by logOut");
        messenger.checkOwners();
        synchronized (messenger) {
            messenger.owned();
            log.debug("messenger locked  by logOut!");
            messenger.write(request);
            log.info("Request sent: \n" + request);
            messenger.leaving();
        }
            return messenger.getNextResponse();
    }

    private void addTaskRequest(Task task) {
        try {
            ResponseParser response = null;
            
            String request = NetProtocol.ClientSide.requestTaskAdd(task);
            log.debug("trying to lock messenger by addTaskRequest");
            messenger.checkOwners();
            synchronized (messenger) {
                messenger.owned();
                log.debug("messenger locked  by addTaskRequest!");
                messenger.write(request);
                log.info("Request sent: \n" + request);

                //return messenger.getNextResponse();
                //ResponseParser response = addTaskRequest(task);
                response = messenger.getNextResponse();
                messenger.leaving();
            }
            if (!response.isOk()) {
                log.info("addingTask action failed.");
                throw new InternalControllerException("addingTask action failed.");
            }
        } catch (IOException ex) {
            log.error("Got errors.", ex);
            throw new InternalControllerException("Got IO errors.", ex);
        }
    }

    /**
     * just editing
     * @param task MUST HAVE ID of !editing! task and data for overriding
     */
    private void editTaskRequest(Task task) {
        try {
            String request = NetProtocol.ClientSide.requestTaskEdit(task);
            log.debug("trying to lock messenger by editTaskRequest");
            synchronized (messenger) {
                log.debug("messenger locked  by editTaskRequest!");
                messenger.write(request);
                log.info("Request sent: \n" + request);
                //return messenger.getNextResponse();

                ResponseParser response = messenger.getNextResponse();
                if (!response.isOk()) {
                    log.info("editingTask action failed.");
                    throw new InternalControllerException("editingTask action failed.");
                }
            }
        } catch (IOException ex) {
            log.error("Got errors.", ex);
            throw new InternalControllerException("Got IO errors.", ex);
        }
    }

    private void removeTaskRequest(String id) {
        try {
            String request = NetProtocol.ClientSide.requestTaskRemove(id);
            log.debug("trying to lock messenger by removeTaskRequest");
            messenger.checkOwners();
            synchronized (messenger) {
                messenger.owned();
                log.debug("messenger locked  by removeTaskRequest!");
                messenger.write(request);
                log.info("Request sent: \n" + request);
                //return messenger.getNextResponse();
                ResponseParser response = messenger.getNextResponse();
                if (!response.isOk()) {
                    log.info("removingTask action failed.");
                    throw new InternalControllerException("removingTask action failed.");
                }
                messenger.leaving();
            }
        } catch (IOException ex) {
            log.error("Got errors.", ex);
            throw new InternalControllerException("Got IO errors.", ex);
        }
    }

    private synchronized Collection<Task> getTasksRequest() {
        try {
            String request = null;
            ResponseParser response = null;
                    
            log.debug("trying to lock messenger by getTasksRequest");
            messenger.checkOwners();
            synchronized (messenger) {
                messenger.owned();
                log.debug("messenger locked by getTasksRequest!");
                request = NetProtocol.ClientSide.requestGetTasks();
                messenger.write(request);
                log.info("Request sent: \n" + request);

                response = messenger.getNextResponse();
                messenger.leaving();
            }
            if (!response.isOk() && response.hasFault()) {
                throw new InternalControllerException(response.getFaultString() + ((response.hasFaultArgument()) ? " : " + response.getFaultArgument() : "."));
            } else {
                log.info("Got tasks.");
                return response.getTasks();
            }
        } catch (IOException ex) {
            log.error("Have troubles in sending/receiving message", ex);
            /*
             * Example of sending stop message to client, but if server doesn't respond, it stops itself.
             */
//            try {
//                messenger.write(NetProtocol.ServerSide.requestServStopped("Have troubles in sending/receiving message"));
//            } catch (IOException ex1) {
//                log.error("attempt to send stop message failed");
//            }
            throw new InternalControllerException("Have troubles in sending/receiving message", ex);
        }
    }

    public ClientSideProxy(ConfigReader configReader) {
        config = configReader;
        init();
    }

    @Override
    public void addTask(Task task) throws InvalidTaskException, InternalControllerException {
        addTaskRequest(task);
    }

    @Override
    public void addTask(String name, String description, String date) throws InvalidTaskException, InternalControllerException {
        try {
            addTaskRequest(new Task(name, description, date));
        } catch (ParseException ex) {
            log.error("Parse date error");
            throw new InternalControllerException("Parse date error");
        }
    }

    @Override
    public void addTask(String id, String name, String description, String date) throws InvalidTaskException, InternalControllerException {
        try {
            addTaskRequest(new Task(id, name, description, date));
        } catch (ParseException ex) {
            log.error("Parse date error");
            throw new InternalControllerException("Parse date error");
        }
    }

    @Override
    public void addPostponedTask(Task task) throws InvalidTaskException, InternalControllerException {
        addTaskRequest(task);
    }

    @Override
    public void removeTask(Task task) throws InternalControllerException {
        removeTaskRequest(task.getId());
    }

    @Override
    public void editTask(Task was, Task become) throws InvalidTaskException, InternalControllerException {
        removeTask(was);
        addTask(become);
    }

    @Override
    public synchronized Collection<Task> getTasks() {
        return getTasksRequest();
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void stopService() {
        try {
            if (connection.isConnected() && !connection.isOutputShutdown() && messenger != null) {
                logOut(login);
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } finally {
            stop = true;
        }
    }

    @Override
    public String getDateFormat() {
        //FIXME make requesting here
        return "yyyy-MM-dd HH:mm:ss";
    }

    @Override
    public void removeTask(String id) throws InternalControllerException {
        removeTaskRequest(id);
    }

    @Override
    public void editTask(String idTaskWas, Task become) throws InvalidTaskException, InternalControllerException {
        editTaskRequest(new Task(idTaskWas, become.getName(), become.getDescription(), become.getDate()));
    }

    @Override
    public void editTask(String id, String name, String description, String date) throws InvalidTaskException, InternalControllerException {
        try {
            editTaskRequest(new Task(id, name, description, date));
        } catch (ParseException ex) {
            log.error("Date parsing error.");
            throw new InternalControllerException("Date parsing error.");
        }
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

    private void init() {
        try {
            ClientSets clientSets = config.getClientSets();
            String host = clientSets.getHost();
            Integer port = clientSets.getPort();

            connection = new Socket(InetAddress.getByName(host), port);
            inputStream = connection.getInputStream();

            messenger = new Messenger(connection);

            ResponseParser resp = null;

            do {
                //login = "admin";                // XXX because debuging

                login = JOptionPane.showInputDialog("Input login", "admin");  //XXX because loading on the same pc with same config file
                //login = clientSets.getLogin();

                log.debug("set login to \"" + login + "\"");

                resp = logIn(login);
                if (!resp.isOk()) {
                    JOptionPane.showMessageDialog(null, resp.getFaultString() + ":" + resp.getFaultArgument());
                    int choose = JOptionPane.showConfirmDialog(null, "Login procedure return an error, want do try again?", "Choose action", JOptionPane.YES_NO_OPTION);
                    if (choose == JOptionPane.NO_OPTION) {
                        System.exit(0);
                    }
                }
            } while (!resp.isOk());
        } catch (UnknownHostException ex) {
            log.error("Cannot connect the unknown host.", ex);
            throw new InternalControllerException("Cannot connect the unknown host.", ex);
        } catch (IOException ex) {
            log.error("Socket or Stream opening error.", ex);
            throw new InternalControllerException("Socket or Stream opening error.", ex);
        } catch (HeadlessException ex) {
            log.error("Mistic exception", ex);
            throw new InternalControllerException("Mistic exception", ex);
        } catch (InternalControllerException ex) {
            log.error(ex);
            throw ex;
        }
    }

    @Override
    public void run() {
        MessageParser msg = null;
        try {
            do {
                try {
                    do {

                        log.debug("(idle reader) in stream available = " + inputStream.available());
                        while (inputStream.available() == 0) {
                            Thread.sleep(100);
                        }
                        log.debug("(idle reader) in stream available = " + inputStream.available());

                        log.debug("trying to lock messenger by idleReader (run method)");
                        messenger.checkOwners();
                        synchronized (messenger) {
                            messenger.owned();
                            msg = messenger.getNextMessage();
                            log.debug("Message readed.");
                            messenger.leaving();
                        }
                        if (msg.isRequest()) {
                            RequestParser request = new RequestParser(msg);

                            if (request.getMethodName().equals("serviceStoppedNotify")) {
                                doServiceStoppedNotify((String) request.getParametr());
                            }
                            if (request.getMethodName().equals("banNotify")) {
                                doBanNotify();
                            }
                            if (request.getMethodName().equals("taskNotify")) {
                                doTaskNotify((Task) request.getParametr());
                            }
                            if (request.getMethodName().equals("update")) {
                                doUpdate();
                            }
                        }

                    } while (!stop);
                } catch (IOException ex) {
                    log.error("ClientSide Socket Connection Error", ex);
                    //throw ex; // TODO: write your own exeption;
                } catch (InterruptedException ex) {
                    log.error("interrupt error", ex);
                } catch (ParseException ex) {
                    log.error("Command parsing error.", ex);
                } catch (InternalControllerException ex) {
                    log.error(ex);
                }
            } while (connection.isConnected() && !connection.isInputShutdown() && !connection.isOutputShutdown());
        } finally {
            try {
                if (connection != null & !connection.isClosed()) {
                    if (messenger != null) {
                        messenger.close();
                        log.info("messenger closing");
                    }
                    //connection.shutdownInput();
                    //connection.shutdownOutput();
                    connection.close();
                    log.info("connection closed");
                }
            } catch (IOException ex) {
                log.error("Closing error", ex);
            }
        }
    }

    private void doServiceStoppedNotify(String reason) {
        JOptionPane.showMessageDialog(null, "Service stopped and will halt, reason : " + reason);
        stopService();
    }

    private void doBanNotify() {
        JOptionPane.showMessageDialog(null, "Service stopped and will halt, reason : YOU ARE BANNED.");
        stopService();
    }

    private void doTaskNotify(Task task) {
        for (Listener l : listeners) {
            if (l instanceof Notifiable) {
                ((Notifiable) l).taskNotify(task);
            }
        }
    }

    private void doUpdate() {
        for (Listener l : listeners) {
            if (l instanceof Updatable) {
                ((Updatable) l).update();
            }
        }
    }
}
