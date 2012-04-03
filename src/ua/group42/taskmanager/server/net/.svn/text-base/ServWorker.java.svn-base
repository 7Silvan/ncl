package ua.group42.taskmanager.server.net;

import ua.group42.taskmanager.common.net.comm.NetProtocol;
import ua.group42.taskmanager.common.net.comm.MessageParser;
import ua.group42.taskmanager.common.net.comm.RequestParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.Collection;
import org.apache.log4j.Logger;
import ua.group42.taskmanager.common.configuration.BadConfigException;
import ua.group42.taskmanager.common.control.ControllerIface;
import ua.group42.taskmanager.common.control.TaskController;
import ua.group42.taskmanager.common.model.Task;
import ua.group42.taskmanager.common.net.comm.Messenger;
import ua.group42.taskmanager.server.ServerWrapperIface;

/**
 *
 * @author Silvan
 */
public class ServWorker implements Runnable {

    private static final Logger log = Logger.getLogger(ServWorker.class);
    private Socket clientSocket = null;
    private ServerWrapperIface server = null;
    private ControllerIface taskController = null;
    private Messenger messenger = null;
    private String login = null;
    private Boolean loggedIn = false;
    private InputStream inputStream = null;
    private Boolean updated = false;
    private Boolean doUpdate = false;

    public Boolean isLoggedIn() {
        return loggedIn;
    }

    private void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    
    public String getLogin() {
        return login;
    }

    public ServWorker(ServerWrapperIface server, Socket connection) throws IOException {
        this.clientSocket = connection;
        this.server = server;
        this.inputStream = connection.getInputStream();
        try {
            messenger = new Messenger(connection);
        } catch (IOException ex) {
            log.fatal("Cannot open streams to communicate with server.", ex);
            System.exit(0);
        } catch (Exception ex) {
            log.fatal("Something horrible happened", ex);
            System.exit(0);
        }
    }

    private String logIn(String login) throws IllegalAccessException, IOException {
        String response = null;

        if (server.canConnect(login)) {
            try {
                server.getConfig().setUserName(login);
            } catch (BadConfigException impossible) {
                log.error("impossible error occured");
            }
            taskController = new TaskController(server.getConfig());
            // TODO: link client as listener

            taskController.addListener(new ServerProxy(this));
            server.doConnect(login);
            this.login = login;
            setLoggedIn(true);
            response = NetProtocol.ServerSide.responseOK();
            log.info(login + " Logged in.");
        } else {
            response = NetProtocol.ServerSide.responseError(NetProtocol.WRONG_LOGIN_NAME_ERROR, login);
        }

        //messenger.write(response);
        return response;
//        }
    }

    private String logOut(String login) throws IllegalAccessException {

        String response = null;
        if (isLoggedIn() && this.login.equals(login)) {
            response = NetProtocol.ServerSide.responseOK();
            taskController.stopService();
            server.doDisConnect(login);
            setLoggedIn(false);
        } else {
            response = NetProtocol.ServerSide.responseError(NetProtocol.CANNOT_PROVIDE_ACTION_ERROR, login);
        }
        return response;
    }

    private String getTasks() throws IOException {

        String response = null;
        if (isLoggedIn()) {
            Collection<Task> tasks = taskController.getTasks();
            response = NetProtocol.ServerSide.responseTransferTasks(tasks);
        } else {
            if (clientSocket.isConnected() && !clientSocket.isOutputShutdown()) {
                response = NetProtocol.ServerSide.responseError(NetProtocol.CANNOT_PROVIDE_ACTION_ERROR, "getTasks");
            }
        }
        log.debug("Sending message: \n" + response);
        return response;
    }

    private String removeTask(String id) throws IOException {

        String response = null;
        if (isLoggedIn()) {
            taskController.removeTask(id);
            response = NetProtocol.ServerSide.responseOK();
        } else {
            response = NetProtocol.ServerSide.responseError(NetProtocol.CANNOT_PROVIDE_ACTION_ERROR, "removeTasks");
        }

        return response;
    }

    private String addTask(Task task) throws IOException {

        String response = null;
        if (isLoggedIn()) {
            taskController.addTask(
                    task.getName(),
                    task.getDescription(),
                    task.getStringDate());
            response = NetProtocol.ServerSide.responseOK();
        } else {
            response = NetProtocol.ServerSide.responseError(NetProtocol.CANNOT_PROVIDE_ACTION_ERROR, "addTasks");
        }


        log.debug("addTask returned response: \n" + response);
        return response;
    }

    private String editTask(Task task) throws IOException {

        String response = null;

        if (isLoggedIn()) {
            taskController.editTask(task.getId(), task);
            response = NetProtocol.ServerSide.responseOK();
        } else {
            response = NetProtocol.ServerSide.responseError(NetProtocol.CANNOT_PROVIDE_ACTION_ERROR, "editTasks");
        }

        return response;
    }

    @Override
    public void run() {
        try {
            do {
                int errorCount = 0;  // counts errors running

                do {
                    try {
                        MessageParser msg = null;
                        log.debug("(idle reader) in stream available = " + inputStream.available());
                        while (inputStream.available() == 0) {
                            Thread.sleep(100);
                        }
                        log.debug("(idle reader) in stream available = " + inputStream.available());


                        log.debug("trying to lock messenger by idleReader");
                        messenger.checkOwners();
                        synchronized (messenger) {
                            messenger.owned();
                            log.debug("locked messenger by idleReader");
                            log.debug("Scanning message in worker (" + login + ")");
//                        String message = (String) messenger.reader.readObject();
//                        log.debug("message readed : \n" + message);
//                        if (message == null || message.equals("")) {
//                            continue;
//                        }


//                        SAXBuilder builder = new SAXBuilder();
//                        Document doc = builder.build(new StringReader(message));

//                        MessageParser msg = new MessageParser(doc);
                            msg = messenger.getNextMessage();

                            if (msg.isRequest()) {
                                RequestParser request = new RequestParser(msg);

                                if (request.getMethodName().equals("logIn")) {
                                    messenger.write(logIn((String) request.getParametr()));
                                }
                                if (request.getMethodName().equals("logOut")) {
                                    messenger.write(logOut((String) request.getParametr()));
                                }
                                if (request.getMethodName().equals("getTasks")) {
                                    messenger.write(getTasks());
                                }
                                if (request.getMethodName().equals("addTask")) {
                                    messenger.write(addTask((Task) request.getParametr()));
                                }
                                if (request.getMethodName().equals("editTask")) {
                                    messenger.write(editTask((Task) request.getParametr()));
                                }
                                if (request.getMethodName().equals("removeTask")) {
                                    messenger.write(removeTask((String) request.getParametr()));
                                }
                            }
                            if (doUpdate) {
                                doUpdate();
                            }
                            messenger.leaving();
                        }
                        errorCount = 0;
                    } catch (ParseException ex) {
                        log.error("Command parsing error.", ex);
                    } catch (IllegalAccessException ex) {
                        //log.error("Cannot access with given userName :" + request.getParametr());
                        log.error(ex.getMessage());
                    } catch (InterruptedException ex) {
                        log.error("interrupt error", ex);
                    } catch (IOException ex) {
                        errorCount++;
                        log.error("Socket (" + ((clientSocket.isClosed()) ? "dead" : "alive") + ") i/o streams error occured", ex);
                        try {
                            /* 
                             * here is the shame, and I think I don't understand something (cause here must be something else)
                             * not such ugly counter of errors, I think.
                             */
                            if (errorCount > 3) {
                                logOut(login);
                                taskController.stopService();
                                // TODO: move this logic to method of stopService() of ControllerIface
                                log.fatal("More than 3 remote errors occured in succession. Connection and user session closed.");
                                break;
                            }
                        } catch (IllegalAccessException ex2) {
                            log.error(ex2.getMessage());
                        }
                    }
                } while (loggedIn);
                log.info("Closing streams.");
            } while (clientSocket.isConnected() && loggedIn);
        } finally {
            try {
                if (messenger != null) {
                    messenger.close();
                    log.info("messager closing");
                }
                if (clientSocket != null) {
                    clientSocket.close();
                    log.info("connection closed");
                }
            } catch (IOException ex) {
                log.error("Closing streams error", ex);
            }
        }
    }

    public void update() {
        doUpdate = true;
    }

    public void doUpdate() {
        if (loggedIn/* && !updated*/) {
            try {
                doUpdate = false;
                log.debug("trying to lock messenger by update");
                messenger.checkOwners();
                synchronized (messenger) {
                    messenger.owned();
                    log.debug("locked messenger by update");

                    messenger.write(NetProtocol.ServerSide.requestUpdate());
                    log.info("Update request sent to Client(" + login + ")");
                    updated = true;//FIXME: crutch

                    /*
                     * Timer for waiting response
                     *  is that needed? in protocol I wrote "MAYBE sever wante to catch response after such requests" :)
                     */
                    messenger.leaving();
                }
            } catch (IOException ex) {
                log.error("Writing requestUpdate in stream error.", ex);
            }
        } else {
            log.info("Cannot send requestUpdate - Client (" + login + ") logged out.");
        }
    }
    
    public void doLogOut() {
            if (loggedIn) {
            try {
                log.debug("trying to lock messenger by update");
                messenger.checkOwners();
                synchronized (messenger) {
                    messenger.owned();
                    log.debug("locked messenger by update");

                    messenger.write(NetProtocol.ServerSide.requestClientBanned());
                    log.info("Ban request sent to Client(" + login + ")");

                    messenger.leaving();
                }
                //loggedIn = false;
            } catch (IOException ex) {
                log.error("Writing requestUpdate in stream error.", ex);
            }
        } else {
            log.info("Cannot send requestClientBanned - Client (" + login + ") logged out.");
        }
    }

    public void taskNotify(Task task) {
        if (loggedIn) {
            try {
                log.debug("trying to lock messenger by taskNotify");
                messenger.checkOwners();
                synchronized (messenger) {
                    log.debug("locked messenger by update");
                    updated = false; //FIXME: crutch
                    messenger.write(NetProtocol.ServerSide.requestTaskNotify(task));
                    log.info("Update request sent to Client");
                }
                doUpdate();
            } catch (IOException ex) {
                log.error("Writing requestUpdate in stream error.", ex);
            }
        } else {
            log.info("Cannot send request - Client logged out.");
        }
    }

    void stopService() {
            doLogOut();
    }
}
