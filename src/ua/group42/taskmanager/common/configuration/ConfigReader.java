package ua.group42.taskmanager.common.configuration;

import java.io.FileWriter;
import org.jdom.*;
import java.util.List;
import org.apache.log4j.*;
import org.jdom.input.SAXBuilder;

import ua.group42.taskmanager.common.tools.Tools;

import ua.group42.taskmanager.common.configuration.Config.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import ua.group42.taskmanager.common.control.InternalControllerException;
import ua.group42.taskmanager.common.control.data.WritingFileException;

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
    private static String configFileName = null;
    private Document doc;

    public synchronized static ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    private ConfigReader() {
    }

    public void readConfig(String configFileName) throws IOException, BadConfigException {
        this.configFileName = configFileName;
        Boolean valid = Tools.valXML(configFileName);
        if (valid) {
            config = new Config();
            try {
                SAXBuilder builder = new SAXBuilder();
                builder.setEntityResolver(
                        new EntityResolver() {

                            @Override
                            public InputSource resolveEntity(String publicid, String sysid) {
                                return new InputSource("ConfigDTD.dtd");
                            }
                        });
                doc = builder.build(configFileName);

                Element rootElement = doc.getRootElement();

                Element dao = rootElement.getChild("dao");
                DaoSets daoS = config.setDaoSets(dao.getAttributeValue("class"));
                List<Element> daoUsers = dao.getChildren("userParams");
                for (Element user : daoUsers) {
                    String userName = user.getAttributeValue("name");
                    List<Element> userParams = user.getChildren("param");
                    Map<String, String> params = new HashMap<String, String>();
                    for (Element param : userParams) {
                        params.put(param.getAttributeValue("name"),
                                param.getText());
                    }
                    daoS.addUser(userName, params);
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

    public Config getConfig() {
        return config;
    }

    public String getFileName() {
        if (config == null) {
            throw new IllegalAccessError("ConfigFile wasn't read yet.");
        }
        String path = null;
        try {
            path = config.getDaoSets().getUsersStringParam(getUserName(), "taskFile");
        } catch (BadConfigException ex) {
            log.error("Errors with extracting params from config", ex);
            throw new InternalControllerException("Errors with extracting params from config", ex);
        }
        if (path == null) {
            log.error("Params of user doesnt contains path to taskFile");
            throw new InternalControllerException("Params of user doesnt contains path to taskFile");
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
        if (config == null) {
            return DEFAULT_DATE_FORMAT;
        }
        String value = config.getParam(DATE_FORMAT_NAME);
        return (value == null ? DEFAULT_DATE_FORMAT : value);
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

    public String getUserName() {
        return config.getUserName();
    }

    public String getParam(String name) {
        return config.getParam(name);
    }

    public Set<String> getUserNames() {
        return config.getDaoSets().getUserNames();
    }

    public void regDaoUser(String userName) throws BadConfigException {
        // adding user to paramCrowds Map
        getConfig().getDaoSets().addUser(userName, new HashMap<String, String>());
        // adding user into doc to dao scope
        Element newUser = new Element("userParam").setAttribute("name", userName);
        newUser.addContent(new Element("param").setAttribute("name", "banned").addContent("false"));
        doc.getRootElement().getChild("dao").addContent(newUser);
        //rewrite configFile
        rewriteConfig();
    }

    public void delDaoUser(String userName) throws BadConfigException {
        // removing user from paramCrowds Map
        getConfig().getDaoSets().delUser(userName);
        // deleting user in doc from dao scope
        Iterator<Element> it = doc.getRootElement().getChild("dao").getChildren("userParams").iterator();
        while (it.hasNext()) {
            if (it.next().getAttributeValue("name").equals(userName)) {
                it.remove();
                break;
            }
        }
        // rewrite configFile
        rewriteConfig();
    }

    private void rewriteConfig() {
        PrintWriter writer = null;
        try {
            XMLOutputter outPutter = new XMLOutputter(Format.getPrettyFormat().setIndent("    ").setLineSeparator("\n"));
            outPutter.output(doc, new FileWriter(configFileName));
        } catch (IOException ex) {
            log.error("Error with writing userList", ex);
            throw new WritingFileException("Error with writing userList", ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {
                log.error("Error closing userList", ex);
                throw new WritingFileException("Error closing userList", ex);
            }
        }
    }

    public void banDaoUser(String userName) throws BadConfigException {
        // marking user as banned
        getConfig().getDaoSets().setUsersStringParam(userName, "banned", "true");
        
        Iterator<Element> it = doc.getRootElement().getChild("dao").getChildren("userParams").iterator();
        while (it.hasNext()) {
            Element user = it.next();
            if (user.getAttributeValue("name")!=null && user.getAttributeValue("name").equals(userName)) {
                it = user.getChildren("param").iterator();
                while(it.hasNext()) {
                    Element param = it.next();
                    if (param.getAttributeValue("name").equals("banned"))
                    {
                        param.setText("true");
                        break;
                    }
                }
                break;
            }
        }
        // rewrite configFile
        rewriteConfig();
    }
    
    public void unBanDaoUser(String userName) throws BadConfigException {
        // marking user as not banned
        getConfig().getDaoSets().setUsersStringParam(userName, "banned", "false");
        
        Iterator<Element> it = doc.getRootElement().getChild("dao").getChildren("userParams").iterator();
        while (it.hasNext()) {
            Element user = it.next();
            if (user.getAttributeValue("name")!=null && user.getAttributeValue("name").equals(userName)) {
                it = user.getChildren("param").iterator();
                while(it.hasNext()) {
                    Element param = it.next();
                    if (param.getAttributeValue("name").equals("banned"))
                    {
                        param.setText("false");
                        break;
                    }
                }
                break;
            }
        }
        // rewrite configFile
        rewriteConfig();
    }
}
