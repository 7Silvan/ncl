package ua.group42.taskmanager.tools;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * This class calls for xml related things, such as validation, etc.
 * If catching Fatal Error - stops with logging Exception 
 * (#see SimpleErrorHandler)
 * @author Group42
 */
public class Tools {
    
    private static final Logger log = Logger.getLogger(Tools.class);
    
    /**
     * This method validates file of XML with related DTD
     * @param valFileXML  string with path of validating file
     * @return true if document is valid, 
     * false otherwise (with logging an error)
     * @throws IOException when file wasn't found or 
     * errors with opening occured
     */
    public static boolean valXML(String valFileXML) throws IOException {
        
        if (!new File(valFileXML).exists()) {
            throw new IOException("File \""+valFileXML+"\" was not found");
        } else {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setValidating(true);
                factory.setNamespaceAware(true);

                SAXParser parser = factory.newSAXParser();

                XMLReader reader = parser.getXMLReader();
                reader.setErrorHandler(new SimpleErrorHandler());
                reader.parse(new InputSource(valFileXML));
                log.info("File \""+valFileXML+"\" is valid");

                return true;
            } catch (ParserConfigurationException ex){
                log.error(null, ex);
            } catch (SAXException ex) {
                
            }
        }
        log.info("File \""+valFileXML+"\" is NOT valid");
        return false;
    }
}
