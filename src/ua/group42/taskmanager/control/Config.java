package ua.group42.taskmanager.control;

import java.util.HashMap;
import java.util.Map;
import ua.group42.taskmanager.control.ConfigReader.ResType;
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

    abstract class DaoSets {

        /**
         * Constructing DAO setting with params
         * @param path of DAO File
         */
        DaoSets(String path) throws BadConfigException {
            if (path == null) {
                throw new BadConfigException("null path");
            }
            this.path = path;
        }
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
    }

    /**
     * Class in charge of DAO XML Setting Group 
     * DaoXmlSets is acronym
     */
    public class DaoXmlSets extends DaoSets {

        public DaoXmlSets(String path) throws BadConfigException {
            super(path);
        }
    }

    /**
     * Class in charge of DAO Serialization Setting Group
     * DaoCsvSets is acronym
     */
    public class DaoCsvSets extends DaoSets {

        public DaoCsvSets(String path) throws BadConfigException {
            super(path);
        }
    }

    /**
     * Class in charge of DAO Data Base Settings Group
     * DaoCsvSets is acronym
     */
    public class DaoDataBaseSets extends DaoSets {

        private final String port;
        private final String login;
        private final String pass;

        public DaoDataBaseSets(String path, String port, String login, String pass) throws BadConfigException {
            super(path);
            this.port = port;
            this.login = login;
            this.pass = pass;
        }
    }
    /**
     * 
     */
    DaoDataBaseSets ddbs;

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
     * Setting in charge of choice of DAO uses in app
     */
    private ResType choice;

    /**
     * Get the value of choice
     *
     * @return the value of choice
     */
    ResType getChoice() {
        return choice;
    }

    /**
     * Set the value of choice with ResType value
     *
     * @param choice new value of choice (ResType)
     */
    void setChoice(ResType choice) {
        this.choice = choice;
    }

    /**
     * Set the value of choice with String value
     * (for example when reading from config file)
     * @param choice new value of choice (String)
     */
    void setChoice(String choice) {
        this.choice = ResType.valueOf(choice);
    }
    /**
     * Object of DAO Data Base Settings 
     */
    DaoCsvSets dcs;

    /**
     * Get the value of DAO Data Base Settings 
     *
     * @return the value of DAO Data Base Settings 
     */
    private DaoDataBaseSets getDaoDataBaseSets() {
        return ddbs;
    }

    /**
     * Set the value of dao of XML
     *
     * @param dxs dao of XML
     */
    void setDaoDataBaseSets(String path, String port, String login, String pass) throws BadConfigException {
        this.ddbs = new DaoDataBaseSets(path, port, login, pass);
    }

    /**
     * Get the value of dcs
     *
     * @return the value of dcs
     */
    private DaoCsvSets getDaoCsvSets() {
        return dcs;
    }
    /**
     * Object of DAO XML Sets
     */
    DaoXmlSets dxs;

    /**
     * Get the value of dxs
     *
     * @return the value of dxs
     */
    private DaoXmlSets getDaoXmlSets() {
        return dxs;
    }

    /**
     * Set the value of dao of XML
     *
     * @param dxs dao of XML
     */
    void setDaoXmlSets(String path) throws BadConfigException {
        this.dxs = new DaoXmlSets(path);
    }

    /**
     * Set the value of dao of Serialization
     *
     * @param dxs dao of Serialization
     */
    void setDaoCsvSets(String path) throws BadConfigException {
        this.dcs = new DaoCsvSets(path);
    }

    // TODO: guarantyy the proper constructing of Config
    String getPath() throws BadConfigException {
        switch (choice) {
            case CSV:
                // TODO: move this logic to constructing of config
//                if (getDaoCsvSets() == null) {
//                    log.error(
//                            new BadConfigException("Wrong choice or lack of config settings"));
//                }
                return getDaoCsvSets().getPath();
            case XML:
                return getDaoXmlSets().getPath();
            default:
                throw new BadConfigException("Choice Assertion error");
        }
    }

    /**
     * Validates if config constructed in a proper way
     */
    boolean validate() throws BadConfigException {
        switch (choice) {
            case CSV:
                if (getDaoCsvSets() == null) {
                    return false;
                }
                break;
            case XML:
                if (getDaoXmlSets() == null) {
                    return false;
                }
                break;
            default:
                throw new BadConfigException("Choice Assertion error");
        }
        return true;
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
