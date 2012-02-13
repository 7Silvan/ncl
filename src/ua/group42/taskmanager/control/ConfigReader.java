package ua.group42.taskmanager.control;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ua.group42.taskmanager.tools.Tools;
import ua.group42.taskmanager.control.Config.*;

/**
 * Main Configurator, reads and accepts params from the fileOfConfig.
 * @author Group42
 */
public class ConfigReader {
    private static final String POSTPONE_MINUTES_NAME = "postponeMinutes";
    private static final Integer DEFAULT_POSTPONE = 1;
    private static final String DATE_FORMAT_NAME = "dateFormat";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final Logger log = Logger.getLogger(ConfigReader.class);
    private static ConfigReader instance = null;
    private static Config config = null;

    public static ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    private ConfigReader() {
    }
    
    public void readConfig(String configFileName) throws IOException, BadConfigException {
        Boolean valid = Tools.valXML(configFileName);
        if (valid) {
            config = new Config();
            try {
                File file = new File(configFileName);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(file);
                doc.getDocumentElement().normalize();

                NodeList arch = doc.getElementsByTagName("arch");

                String archS = arch.item(0).getAttributes().getNamedItem("type").getNodeValue();
                config.setArchitecture(Config.Architecture.valueOf(archS.toUpperCase()));

                NodeList daoGroup = doc.getElementsByTagName("daogroup");

                String choice = daoGroup.item(0).getAttributes().getNamedItem("choice").getNodeValue();
                config.setChoice(ResType.valueOf(choice.toUpperCase()));

                NodeList daos = daoGroup.item(0).getChildNodes();
                for (int i = 0; i < daos.getLength(); i++) {
                    if (daos.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        String name = daos.item(i).getNodeName();
                        if ("daoxml".equals(name)) {
                            String path = ((Element)daos.item(i)).getElementsByTagName("path").item(0).getTextContent();
                            
                            config.setDaoXmlSets(path);
                        }
                        if ("daocsv".equals(name)) {
                            String path = ((Element)daos.item(i)).getElementsByTagName("path").item(0).getTextContent();
                            config.setDaoCsvSets(path);
                        }
                        if ("daodb".equals(name)) {
                            Element dbParams = (Element) daos.item(i);
                            String path = dbParams.getElementsByTagName("path").item(0).getTextContent();
                            String port = dbParams.getElementsByTagName("port").item(0).getTextContent();
                            String login = dbParams.getElementsByTagName("login").item(0).getTextContent();
                            String pass = dbParams.getElementsByTagName("pass").item(0).getTextContent();
                            config.setDaoDataBaseSets(path, port, login, pass);
                        }
                    }
                }

                NodeList params = doc.getElementsByTagName("param");
                for (int k = 0; k < params.getLength(); k++) {
                    Element param = (Element) params.item(k);
                    config.addParam(param.getAttributes().getNamedItem("name").getNodeValue(),
                            param.getFirstChild().getNodeValue());
                }

            } catch (ParserConfigurationException ex) {
                log.error(null, ex);
            } catch (SAXException ex) {
                log.error(null, ex);
//            } catch (IOException ex) {
//                log.error(null, ex);
//            } catch (BadConfigException ex) {
//                log.error(null, ex);
            }
        }
    }

    public ResType getResourcesType() {
        if (config == null) {
            throw new UnsupportedOperationException("ConfigFile wasn't read yet.");
        }
        return config.getChoice();
    }

    public String getFileName() {
        if (config == null) {
            throw new UnsupportedOperationException("ConfigFile wasn't read yet.");
        }
        String path = null;
        try {
            path = config.getPath();
        } catch (BadConfigException ex) {
            log.error(null, ex);
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
        return (pTime == null?  DEFAULT_POSTPONE.intValue() : pTime.intValue() );
    }
    
    /**
     * Reads from parametrs dateFormat
     * @return pattern of Date Format, not validating
     */
    public String getDateFormat() {
        String value = config.getParam(DATE_FORMAT_NAME);
        return (value == null? DEFAULT_DATE_FORMAT : value);
    }

    public static enum ResType {

        DB, CSV, XML
    }
}
