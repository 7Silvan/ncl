package ua.group42.taskmanager.server;

import ua.group42.taskmanager.server.net.MultiConnectCatcher;
import ua.group42.taskmanager.server.view.ServerGui;
import ua.group42.taskmanager.server.view.ServerGuiIface;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.*;
import org.jdom.*;
import ua.group42.taskmanager.common.configuration.BadConfigException;
import ua.group42.taskmanager.common.configuration.Config.DaoSets;
import ua.group42.taskmanager.common.configuration.ConfigReader;
import ua.group42.taskmanager.common.control.InternalControllerException;
import ua.group42.taskmanager.server.model.*;

/**
 *
 * @author Silvan
 */
public class ServerWrapper implements ServerWrapperIface {

    private static final Logger log = Logger.getLogger(ServerWrapper.class);
    private Map<String, UserModel> users = new ConcurrentHashMap<String, UserModel>();
    private Document doc;
    private static ServerWrapper instance;
    private static final int DEFAULT_WORK_PORT = 9000;
    private ServerGuiIface gui; // we have only one view
    private ConfigReader confReader = ConfigReader.getInstance();
    private MultiConnectCatcher conCatcher;
    private final String CONFIG_FILE = ".\\dist-server\\config.xml";
    private Thread stopper = null;
    private Boolean isStopped = false;

    public void secret() {
        conCatcher.secret();
    }

    //<editor-fold defaultstate="collapsed" desc="Manipulations with users">
    public Boolean checkUserExistence(String name) throws IllegalAccessException {
        if (!users.containsKey(name)) {
            log.error("There is no user with such name \"" + name + "\"");
            gui.showError("There is no user with such name \"" + name + "\"");
            throw new IllegalAccessException("There is no user with such name \"" + name + "\"");
        }
        return true;
    }

    @Override
    public Boolean isUserBanned(String name) throws IllegalAccessException {
        checkUserExistence(name);
        return users.get(name).isBanned();

    }

    @Override
    public void banUser(String name) throws IllegalAccessException {
        checkUserExistence(name);
        conCatcher.disconnectUser(name);
        users.get(name).setState(State.BANNED);
         try {
                confReader.banDaoUser(name);
            } catch (BadConfigException ex) {
                log.error("Error with adding user to config.", ex);
                throw new InternalControllerException("Error with adding user to config.", ex);
            }
        gui.update();
        log.info(name + " diconnected and banned");
    }

    /**
     * user has been unBanned become offline status and can try to connect to server
     * @param name of user
     */
    @Override
    public void unBanUser(String name) throws IllegalAccessException {
        checkUserExistence(name);
        users.get(name).setState(State.OFFLINE);
         try {
                confReader.unBanDaoUser(name);
            } catch (BadConfigException ex) {
                log.error("Error with adding user to config.", ex);
                throw new InternalControllerException("Error with adding user to config.", ex);
            }
        gui.update();
    }

    @Override
    public void regUser(String name) throws IllegalAccessException {
        if (!users.containsKey(name)) {
            users.put(name, new UserModel(name));
            gui.update();
            try {
                confReader.regDaoUser(name);
            } catch (BadConfigException ex) {
                log.error("Error with adding user to config.", ex);
                throw new InternalControllerException("Error with adding user to config.", ex);
            }
            log.info("User \"" + name + "\" created.");
        } else {
            log.error("Cannot register the user with such name \"" + name + "\" already exist");
            gui.showError("Cannot register the user with such name \"" + name + "\" already exist");
            throw new IllegalAccessException("Cannot register the user with such name \"" + name + "\" already exist");
        }
    }

    @Override
    public void delUser(String name) throws IllegalAccessException {
        checkUserExistence(name);
        // TODO: doDisConnect and stopService?
        // doDisConnect(name);
        users.remove(name);
        gui.update();
        try {
                confReader.delDaoUser(name);
            } catch (BadConfigException ex) {
                log.error("Error with removing user to config.", ex);
                throw new InternalControllerException("Error with removing user to config.", ex);
            }
    }

    @Override
    public Collection<UserModel> getAllUsers() {
        return users.values();
    }

    @Override
    public Collection getActiveUsers() {
        Collection<UserModel> activeUsers = new ArrayList<UserModel>();
        Iterator<UserModel> it = getAllUsers().iterator();
        while (it.hasNext()) {
            UserModel user = it.next();
            if (user.getState() == State.ONLINE) {
                activeUsers.add(user);
            }
        }
        return null;
    }

    @Override
    public int getWorkPort() {
        Integer port = null;
        try {
            port = Integer.parseInt(confReader.getParam("portWork"));
        } catch (NumberFormatException ex) {
            log.error("Cannot parse port from returned configParam", ex);
        }
        return (port == null) ? DEFAULT_WORK_PORT : port;
    }

    @Override
    public int getStopPort() {
        return Integer.parseInt(confReader.getParam("portStop"));
    }

    @Override
    public Boolean canConnect(String name) throws IllegalAccessException {
        checkUserExistence(name);
        return (users.get(name).getState() == State.OFFLINE);
    }

    @Override
    public Boolean doConnect(String name) throws IllegalAccessException {
        if (canConnect(name)) {
            users.get(name).setState(State.ONLINE);
            log.info(name + " marked as connected.");
            gui.update();
            return true;
        } else {
            gui.showError("Cannot connect. There is no user with name " + name);
            throw new IllegalAccessException("Cannot connect. There is no user with name \"" + name + "\"");
        }
    }

    @Override
    public Boolean doDisConnect(String name) throws IllegalAccessException {
        canDisConnect(name);

        getUser(name).setState(State.OFFLINE);
        log.info(name + " marked as disconnected.");
        gui.update();
        return true;
    }

    @Override
    public Boolean canDisConnect(String name) throws IllegalAccessException {
        checkUserExistence(name);
        return (users.get(name).getState() == State.ONLINE);
    }

    private UserModel getUser(String name) throws IllegalAccessException {
        checkUserExistence(name);
        return users.get(name);
    }

    @Override
    public Boolean isUserOnline(String name) throws IllegalAccessException {
        checkUserExistence(name);
        return (users.get(name).getState() == State.ONLINE);
    }
    //</editor-fold>
////////////////////////////////////////////////////////////////////////////////

    public static void main(String args[]) {
        instance = new ServerWrapper();
    }

    public ServerWrapper() {
        init();
    }

    private void init() {
        try {
            confReader.readConfig(CONFIG_FILE);
            log.debug("Config readed.");

            users = readUsers();

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    gui = new ServerGui(ServerWrapper.this);
                    gui.update();
                }
            });

            conCatcher = new MultiConnectCatcher(this);
            new Thread(conCatcher).start();

            try {
                while (!isStopped) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ex) {
                log.error("Stopper has been interrupted.", ex);
            }

            log.info("stopping server");
            conCatcher.stop();
            log.info("server stopped");
            System.exit(0);
        } catch (BadConfigException ex) {
            log.fatal("Bad Configurations", ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Bad Configurations",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (IOException ex) {
            log.fatal("IO Error reading configFile", ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "IO Error reading configFile",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private Map<String, UserModel> readUsers() {
        Map<String, UserModel> readedUsers = new HashMap<String, UserModel>();
        Set<String> users = confReader.getUserNames();
        DaoSets dao = confReader.getConfig().getDaoSets();
        
        try {
        for (String userName : users) {
            UserModel newUser = new UserModel(userName);
            if (dao.getUsersStringParam(userName, "banned").equals("true"))
                newUser.setState(State.BANNED);
            else
                newUser.setState(State.OFFLINE);
            
            readedUsers.put(userName, newUser);
        }
        } catch (BadConfigException ex) {
            log.error("Error with loading banned param from daoSets", ex);
            throw new InternalControllerException("Error with loading banned param from daoSets", ex);
        }
        
        return readedUsers;
    }
////////////////////////////////////////////////////////////////////////////////
    @Override
    public ConfigReader getConfig() {
        return confReader;
    }

    @Override
    public void stopServer() {
        isStopped = true;
    }
}
