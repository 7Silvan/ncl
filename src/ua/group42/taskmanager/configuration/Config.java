package ua.group42.taskmanager.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.*;

/**
 * This class incapsulates settings for app
 * @author Silvan
 */
public class Config {

    private static final Logger log = Logger.getLogger(Config.class);
    private Map<String, String> paramList = new HashMap<String, String>();
    private String userName;

    public final class DaoSets {

        private Map<String, String> paramList = new HashMap<String, String>();

        /**
         * Constructing DAO settings for using locally for single user
         * @param singleUserPath of DAO File
         */
        public DaoSets(String className, String singleUserPath) throws BadConfigException {
            if (className == null || singleUserPath == null) {
                throw new BadConfigException("null path");
            }
            this.className = className;
            addUserTaskListPath("singleUser", singleUserPath);
        }

        public DaoSets(String className) throws BadConfigException {
            if (className == null) {
                throw new BadConfigException("null path");
            }
            this.className = className;
        }
        
        /**
         * ClassName of DAO
         */
        private String className;

        /**
         * Get the value of path
         *
         * @return the value of path
         */
        public String getPath() throws BadConfigException {
            return getUserTaskListPath(userName);
        }

        /**
         * Get the value of DAO's  ClassName
         *
         * @return the value of DAO's ClassName
         */
        public String getClassName() {
            return className;
        }

        /**
         * add user's task list path associating it with user's name
         * @param name of user
         * @param value is path to file with tasks
         */
        public void addUserTaskListPath(String name, String value) throws BadConfigException {
            if (paramList.containsKey(name)) {
                log.error("userName is not unique");
                throw new BadConfigException("userName is not unique");
            }
            if (paramList.containsValue(value)) {
                log.error("path is already assigned to another user.");
                throw new BadConfigException("path is already assigned to another user.");
            }
            paramList.put(name, value);
        }

        /**
         * gets path of user with given name
         * @param name of needed setting
         * @return value of setting if exists, and null otherwise
         */
        public String getUserTaskListPath(String name) throws BadConfigException {
            String ret = paramList.get(name);
            if (ret == null) {
                log.error("there is no user with such name.");
                throw new BadConfigException("there is no user with such name.");
            }
            return ret;
        }
        
        public Set<String> getUserNames() {
            return paramList.keySet();
        }
    }

    public class ServerSets {

        /**
         * path to file with user logins
         */
        private String path;

        public ServerSets(String path) throws BadConfigException {
            if (path == null) {
                throw new BadConfigException("null path in server specifications");
            }
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    public class ClientSets {

        private String host;
        private Integer port;
        private String login;

        public ClientSets(String host, String port, String login) throws BadConfigException {
            if (host == null || port == null || login == null) {
                throw new BadConfigException("some arguments is null");
            }

            this.host = host;
            this.login = login;

            try {
                this.port = Integer.parseInt(port);
            } catch (NumberFormatException ex) {
                throw new BadConfigException("bad port", ex);
            }
        }

        public String getHost() {
            return host;
        }

        public String getLogin() {
            return login;
        }

        public Integer getPort() {
            return port;
        }
    }

    public Config() {
    }

    /*
     * Sets
     */
    private DaoSets ds;
    private ServerSets ss;
    private ClientSets cs;

    /**
     * Get the  DAO Sets
     *
     * @return the DAO Sets
     */
    public DaoSets getDaoSets() {
        return ds;
    }

    /**
     * Set the DAO Sets (for singleUser use)
     * @param className which will work
     * @param path to file with tasks file
     * @throws BadConfigException throw when have troubles in configuring
     * @return created DaoSets
     */
    public DaoSets setDaoSets(String className, String path) throws BadConfigException {
        ds = new DaoSets(className, path);
        return ds;
    }
    
    /**
     * Set the DAO Sets (for singleUser use), to use it, has to be filled 
     * with user task list's paths to files over the object methods
     * @see DaoSets.addUserTaskListPath
     * @see DaoSets.getUserTaskListPath
     * @param className which will work
     * @param path to file with tasks file
     * @throws BadConfigException throw when have troubles in configuring
     * @return created DaoSets
     */
    public DaoSets setDaoSets(String className) throws BadConfigException {
        ds = new DaoSets(className);
        return ds;
    }

    /**
     * Big shot of our DAO Sets, it desrvers a method :)
     * @return path to file for our DAO
     * @throws BadConfigException if errors in init DAO Sets or params occured
     */
    public String getPath() throws BadConfigException {
        return getDaoSets().getPath();
    }

    /**
     * Big shot of our DAO Sets, it desrvers a method :)
     * @return path to file for our DAO
     * @throws BadConfigException if errors in init DAO Sets or params occured
     */
    public String getClassName() throws BadConfigException {
        return getDaoSets().getClassName();
    }

    /**
     * Validates if DAO Sets not null, potentially could validate more things
     */
    public boolean validate() throws BadConfigException {
        return (getDaoSets() == null
                || getDaoSets().getClassName() == null
                || getDaoSets().getPath() == null) ? false : true;
    }

    /**
     * adds settings to config
     * @param name of setting
     * @param value of setting
     */
    public void addParam(String name, String value) {
        paramList.put(name, value);
    }

    /**
     * gets value of setting of config
     * @param name of needed setting
     * @return value of setting if exists, and null otherwise
     */
    public String getParam(String name) {
        return paramList.get(name);
    }

    public ServerSets getServerSets() {
        return ss;
    }

    public void setServerSets(String path) throws BadConfigException {
        this.ss = new ServerSets(path);
    }

    public ClientSets getClientSets() {
        return cs;
    }

    public void setClientSets(String ip, String port, String login) throws BadConfigException {
        this.cs = new ClientSets(ip, port, login);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
