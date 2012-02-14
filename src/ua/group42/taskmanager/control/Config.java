package ua.group42.taskmanager.control;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.*;

/**
 * This class incapsulates settings for app
 * @author Silvan
 */
class Config {

    private static final Logger log = Logger.getLogger(Config.class);

    /**
     * Enumerates app's behaviours
     */
    enum Architecture {

        CLIENT, SERVER, SINGLE
    };
    
    Map<String, String> paramList = new HashMap<String, String>();

    public class DaoSets {

        /**
         * Constructing DAO setting with params
         * @param path of DAO File
         */
        DaoSets(String className, String path) throws BadConfigException {
            if (className == null || path == null) {
                throw new BadConfigException("null path");
            }
            this.className = className;
            this.path = path;
        }

        /**
         * ClassName of DAO
         */
        private String className;
        
        /**
         * Path of DAO File
         */
        private String path;

        /**
         * Get the value of path
         *
         * @return the value of path
         */
        public String getPath() {
            return path;
        }
        
        /**
         * Get the value of DAO's  ClassName
         *
         * @return the value of DAO's ClassName
         */
        public String getClassName() {
            return className;
        }
    }

    public Config() {
    }
    
    /**
     * Setting in charge of type of app behavior
     */
    private Architecture arch;

    /**
     * Set the value of architecture
     *
     * @param arch new value of architecture
     */
    void setArchitecture(Architecture arch) {
        this.arch = arch;
    }

    /**
     * Get the value of architecture
     *
     * @return the value of architecture
     */
    Architecture getArchitecture() {
        return arch;
    }

    /**
     * Object of DAO Sets
     */
    DaoSets ds;

    /**
     * Get the  DAO Sets
     *
     * @return the DAO Sets
     */
    private DaoSets getDaoSets() {
        return ds;
    }

    /**
     * Set the DAO Sets
     *
     * @param path parametr of the DAO Sets
     */
    void setDaoSets(String className, String path) throws BadConfigException {
        this.ds = new DaoSets(className, path);
    }
    
    /**
     * Big shot of our DAO Sets, it desrvers a method :)
     * @return path to file for our DAO
     * @throws BadConfigException if errors in init DAO Sets or params occured
     */
    String getPath() throws BadConfigException {
        return getDaoSets().getPath();
    }
    
    /**
     * Big shot of our DAO Sets, it desrvers a method :)
     * @return path to file for our DAO
     * @throws BadConfigException if errors in init DAO Sets or params occured
     */
    String getClassName() throws BadConfigException {
        return getDaoSets().getClassName();
    }

    /**
     * Validates if DAO Sets not null, potentially could validate more things
     */
    boolean validate() throws BadConfigException {
        return (getDaoSets() == null 
              ||getDaoSets().getClassName() == null
              ||getDaoSets().getPath() == null)? false : true ;
    }

    /**
     * adds settings to config
     * @param name of setting
     * @param value of setting
     */
    void addParam(String name, String value) {
        paramList.put(name, value);
    }

    /**
     * gets value of setting of config
     * @param name of needed setting
     * @return value of setting if exists, and null otherwise
     */
    String getParam(String name) {
        return paramList.get(name);
    }
}
