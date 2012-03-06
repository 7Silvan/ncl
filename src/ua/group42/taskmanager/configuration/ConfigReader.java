package ua.group42.taskmanager.configuration;

import org.jdom.*;
import java.util.List;
import org.apache.log4j.*;
import org.jdom.input.SAXBuilder;

import ua.group42.taskmanager.tools.Tools;

import ua.group42.taskmanager.configuration.Config.*;
import java.io.IOException;
import ua.group42.taskmanager.control.InternalControllerException;

/**
 * Main Configurator, reads and accepts params from the fileOfConfig.
 * @author Group42
 */
public final class ConfigReader {

    private static final String POSTPONE_MINUTES_NAME = "postponeMinutes";
    private static final Integer DEFAULT_POSTPONE = 1;
    private static final String DATE_FORMAT_NAME = "dateFormat";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final Logger log = Logger.getLogger(ConfigReader.class);
    private static Config config = null;
    private static ConfigReader instance = null;
    private Document doc;
    
    public synchronized static ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    private ConfigReader() {
    }

//    public ConfigReader(String CONFIG_FILE) throws IOException, BadConfigException {
//        readConfig(CONFIG_FILE);
//    }

    public void readConfig(String configFileName) throws IOException, BadConfigException {
        Boolean valid = Tools.valXML(configFileName);
        if (valid) {
            config = new Config();
            try {

                SAXBuilder builder = new SAXBuilder();
                doc = builder.build(configFileName);

                Element rootElement = doc.getRootElement();

                Element dao = rootElement.getChild("dao");
                DaoSets daoS = config.setDaoSets(dao.getChildText("class"));
                List<Element> daoPaths = dao.getChildren("userTaskList");
                for (Element path : daoPaths) {
                    daoS.addUserTaskListPath(
                            path.getAttributeValue("name"), 
                            path.getTextTrim());
                }

                Element server = rootElement.getChild("server");
                if (server != null) {
                    config.setServerSets(server.getChildText("path"));
                }

                Element client = rootElement.getChild("client");
                if (client != null) {
                    config.setClientSets(
                            client.getChildText("ip"),
                            client.getChildText("port"),
                            client.getChildText("login"));
                }

                List<Element> params = rootElement.getChild("params").getChildren();
                for (Element param : params) {
                    config.addParam(param.getAttributeValue("name"),
                            param.getText());
                }


            } catch (JDOMException ex) {
                log.error("XML parsing error", ex);
                throw new InternalControllerException("XML parsing error", ex);
            } catch (IOException ex) {
                log.error("File IO operating error", ex);
                throw new InternalControllerException("File IO operating error", ex);
            } catch (BadConfigException ex) {
                log.error("Config data invalidness", ex);
                throw new InternalControllerException("Config data invalidness", ex);
            }
        }
    }

    public String getDaoClassName() {
        if (config == null) {
            throw new IllegalAccessError("ConfigFile wasn't read yet.");
        }

        String className = null;
        try {
            className = config.getClassName();
        } catch (BadConfigException ex) {
            log.error("Errors with extracting params from config", ex);
            throw new InternalControllerException("Errors with extracting params from config", ex);
        }
        return className;
    }

    public String getFileName() {
        if (config == null) {
            throw new IllegalAccessError("ConfigFile wasn't read yet.");
        }
        String path = null;
        try {
            path = config.getPath();
        } catch (BadConfigException ex) {
            log.error("Errors with extracting params from config", ex);
            throw new InternalControllerException("Errors with extracting params from config", ex);
        }
        return path;
    }

    public int getPostponeTime() {
        String value = config.getParam(POSTPONE_MINUTES_NAME);
        Integer pTime = null;
        try {
            pTime = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            log.error(ex);
        }
        return (pTime == null ? DEFAULT_POSTPONE.intValue() : pTime.intValue());
    }

    /**
     * Reads from parametrs dateFormat
     * @return pattern of Date Format, not validating
     */
    public String getDateFormat() {
        if (config == null)
            return DEFAULT_DATE_FORMAT;
        String value = config.getParam(DATE_FORMAT_NAME);
        return (value == null ? DEFAULT_DATE_FORMAT : value);
    }

    public ServerSets getServerSets() {
        return config.getServerSets();
    }

    public ClientSets getClientSets() {
        return config.getClientSets();
    }
    
    /**
     * Sets userName in context of current using
     * @param name of user for accessing right paths
     * @throws BadConfigException throws is said userName wasn't mentioned in
     * config file
     */
    public void setUserName(String name) throws BadConfigException {
        if (config.getDaoSets().getUserNames().contains(name)) {
            config.setUserName(name);
        } else {
            throw new BadConfigException("unknown user name");
        }
    }
    
    public String getUserName(){
        return config.getUserName();
    }

    public String getParam(String name) {
        return config.getParam(name);
    }
}
