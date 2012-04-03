package ua.group42.taskmanager.common.configuration;

import java.util.*;
import org.apache.log4j.*;

/**
 * This class incapsulates settings for app
 * @author Silvan
 */
public class Config {

    private static final Logger log = Logger.getLogger(Config.class);
    private Map<String, String> paramList = new HashMap<String, String>();
    private String userName;
    /*
     * Sets
     */
    private DaoSets ds;

    public final class DaoSets {

        private Map<String, Map<String, String>> users = new HashMap<String, Map<String, String>>();
        /**
         * ClassName of DAO
         */
        private String className;

        public DaoSets(String className) throws BadConfigException {
            if (className == null) {
                throw new BadConfigException("Cannot create DaoSets - null arguments");
            }
            this.className = className;
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
         * add user's params associating it with userName
         * @param name of user
         * @param params map of user's parameters
         * @throws BadConfigException throws if userName already exists in dao scope
         */
        public void addUser(String name, Map<String, String> params) throws BadConfigException {
            if (users.containsKey(name)) {
                log.error("userName is not unique");
                throw new BadConfigException("userName is not unique");
            }
            if (users.containsValue(params)) {
                log.error("params is already assigned to another user.");
                throw new BadConfigException("params is already assigned to another user.");
            }
            users.put(name, params);
        }
        
        /**
         * removes user's params from daoscope
         */
        public void delUser(String name) throws BadConfigException {
            if (!users.containsKey(name)) {
                log.error("userName is not present in dao scope, name = " + name );
                throw new BadConfigException("userName is not present in dao scope, name = " + name);
            }
            users.remove(name);
        }

        /**
         * gets some string param of user's paramList, accessing by user's name
         */
        public String getUsersStringParam(String name, String param) throws BadConfigException {
            Map<String, String> user = users.get(name);
            if (user == null) {
                log.error("there is no user with such name.");
                throw new BadConfigException("there is no user with such name.");
            }
            return getStringParam(user, param);
        }
        
        public void setUsersStringParam(String name, String param, String value) throws BadConfigException {
            Map<String, String> user = users.get(name);
            if (user == null) {
                log.error("there is no user with such name.");
                throw new BadConfigException("there is no user with such name.");
            }
            user.put(param, value);
        }

        /**
         * Gets some integer param of user's paramList, accessing by user's name
         * @param name of user to access it's param crowd
         * @param param name of param to get
         * @return return integer presented param's value
         * @throws BadConfigException 
         */
        public Integer getUsersIntegerParam(String name, String param) throws BadConfigException {
            Map<String, String> user = users.get(name);
            if (user == null) {
                log.error("there is no user with such name.");
                throw new BadConfigException("there is no user with such name.");
            }
            return getIntegerParam(user, param);
        }

        public Set<String> getUserNames() {
            return users.keySet();
        }

        /**
         * returns String value of parametr
         * @param user name of param crowd
         * @param param name of param
         * @return param's value, null if there is no param with such param's name in user's param crowd
         */
        private String getStringParam(Map<String, String> user, String param) {
            return user.get(param);
        }

        /**
         * returns Integer value of parametr
         * @param user name of param crowd
         * @param param name of param
         * @return param's value, null if there is no param with such param's name in user's peram crowd
         * @throws NumberFormatException if have troubles with parsing Integer
         */
        private Integer getIntegerParam(Map<String, String> user, String param) {
            Integer val = null;
            try {
                if (user.get(param) != null) {
                    val = Integer.parseInt(user.get(param));
                }
            } catch (NumberFormatException ex) {
                log.error("Error intParsing value of parametr (" + param + ") :" + user.get(param), ex);
                throw ex;
            }
            return (val == null) ? null : val;
        }
    }

    public Config() {
    }

    /**
     * Get the  DAO Sets
     *
     * @return the DAO Sets
     */
    public DaoSets getDaoSets() {
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
    public String getClassName() throws BadConfigException {
        return getDaoSets().getClassName();
    }

    /**
     * Validates if DAO Sets not null, potentially could validate more things
     */
    public boolean validate() throws BadConfigException {
        return (getDaoSets() == null
                || getDaoSets().getClassName() == null) ? false : true;
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

    /**
     * gets int value of setting of config
     * @param name of needed setting
     * @return value of setting if exists, and null otherwise
     * @throws NumberFormatException if have troubles with parsing Integer
     */
    public Integer getIntParam(String name) {
        Integer val = null;
        try {
            if (paramList.get(name) != null) {
                val = Integer.parseInt(paramList.get(name));
            }
        } catch (NumberFormatException ex) {
            log.error("Error intParsing value of parametr (" + name + ") :" + paramList.get(name), ex);
            throw ex;
        }
        return (val == null) ? null : val;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
