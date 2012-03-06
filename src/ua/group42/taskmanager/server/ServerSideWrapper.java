package ua.group42.taskmanager.server;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.*;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import ua.group42.taskmanager.configuration.BadConfigException;
import ua.group42.taskmanager.configuration.ConfigReader;
import ua.group42.taskmanager.control.data.WritingFileException;
import ua.group42.taskmanager.server.UserModel.State;
import ua.group42.taskmanager.tools.Tools;

/**
 *
 * @author Silvan
 */
public class ServerSideWrapper implements ServerSideWrapperIface {

    private static final Logger log = Logger.getLogger(ServerSideWrapper.class);
    private Map<String, UserModel> users = new ConcurrentHashMap<String, UserModel>();
    private Document doc;
    private static ServerSideWrapper instance;
    private ServerGuiIface gui; // we have only one view
    private ConfigReader confReader = ConfigReader.getInstance();
    private MultiConnectCatcher conCatcher;
    private String CONFIG_FILE = "config.xml";

    @Override
    public void banUser(String name) {
        users.get(name).setState(UserModel.State.BANNED);
        gui.update();
    }

    /**
     * user has been unBanned become offline status and can try to connect to server
     * @param name of user
     */
    @Override
    public void unBanUser(String name) {
        users.get(name).setState(UserModel.State.OFFLINE);
        gui.update();
    }

    @Override
    public void regUser(String name) {
        if (!users.containsKey(name)) {
            users.put(name, new UserModel(name));
            gui.update();
            writeUsers(confReader.getServerSets().getPath(),
                    users.values());
        } else {
            gui.showError("user with name " + name + " already exist");
        }
    }

    @Override
    public void delUser(String name) {
        if (users.containsKey(name)) {
            users.remove(name);
            gui.update();
            writeUsers(confReader.getServerSets().getPath(),
                    users.values());
        } else {
            gui.showError("there is no user with name " + name);
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
            if (user.getState() == UserModel.State.ONLINE) {
                activeUsers.add(user);
            }
        }
        return null;
    }

    @Override
    public int getWorkPort() {
        return Integer.parseInt(confReader.getParam("portWork"));
    }

    @Override
    public int getStopPort() {
        return Integer.parseInt(confReader.getParam("portStop"));
    }

    @Override
    public boolean canConnect(String name) throws IllegalAccessException {
        if (users.containsKey(name)) {
            return (users.get(name).getState() == State.OFFLINE);
        } else {
            gui.showError("there is no user with name " + name);
            throw new IllegalAccessException("there is no user with name \"" + name + "\"");
        }
    }
    
    @Override
    public UserModel getUser(String name) throws IllegalAccessException {
        if (users.containsKey(name)) {
            return users.get(name);
        } else {
            gui.showError("there is no user with name " + name);
            throw new IllegalAccessException("there is no user with name \"" + name + "\"");
        }
    }

    private void init() {
        try {
            confReader.readConfig(CONFIG_FILE);
            log.debug("Config readed.");

            users = readUsers(confReader.getServerSets().getPath());

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    gui = new ServGUI(ServerSideWrapper.this);
                    gui.update();
                }
            });

            conCatcher = new MultiConnectCatcher(this);
            new Thread(conCatcher).start();

            try {
                Thread stopper = new StopMonitor(Integer.parseInt(confReader.getParam("portStop")));
                stopper.start();
                stopper.join();
            } catch (InterruptedException ex) {
                log.error(ex);
            } catch (NumberFormatException ex) {
                log.fatal("bad parametr, server will fall down");
                System.exit(0);
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

    public ServerSideWrapper() {
        init();
    }

    public static void main(String args[]) {
        instance = new ServerSideWrapper();
    }

    private Map<String, UserModel> readUsers(String path) {
        Map<String, UserModel> readedUsers = new HashMap<String, UserModel>();

        try {
            Boolean valid = Tools.valXML(path);
            if (valid) {
                try {
                    SAXBuilder builder = new SAXBuilder();
                    doc = builder.build(new File(path));

                    Iterator<Element> it = doc.getRootElement().getChildren("user").iterator();

                    while (it.hasNext()) {
                        Element user = it.next();

                        UserModel newUser = new UserModel(user.getTextTrim());
                        newUser.setState(
                                (user.getAttributeValue("banned").equals("true"))
                                ? State.BANNED : State.OFFLINE);

                        readedUsers.put(newUser.getName(), newUser);
                    }
                } catch (JDOMException ex) {
                    log.error("Error in loading tasks from xml ", ex);
                    throw new IOException("Error in loading tasks from xml ", ex);
                }
            }
        } catch (IOException ex) {
            log.error("reading userList file error", ex);
            gui.showError("reading userList file error");
            System.exit(0);
        }
        return readedUsers;
    }

    private void writeUsers(String path, Collection<UserModel> users) {
        PrintWriter writer = null;
        try {
            doc = new Document(new Element("users"), new DocType("users", "users.dtd"));
            Iterator<UserModel> user = users.iterator();
            while (user.hasNext()) {
                UserModel curUser = user.next();
                doc.getRootElement().addContent(new Element("user").addContent(curUser.getName()).setAttribute("banned", (curUser.isBanned()) ? "true" : "false"));
                //writer.println(user.next().getName());
            }
            XMLOutputter outPutter = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n"));

            outPutter.output(doc, new FileWriter(path));
        } catch (IOException ex) {
            log.error("Error with writing userList", ex);
            gui.showError("Error with writing userList");
            throw new WritingFileException("Error with writing userList", ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {
                log.error("Error closing userList", ex);
                gui.showError("Error closing userList");
            }
        }
        return;
    }

    
}
