package ua.group42.taskmanager.clientside;

import java.io.IOException;
import javax.swing.JOptionPane;
import org.apache.log4j.*;
import ua.group42.taskmanager.configuration.BadConfigException;
import ua.group42.taskmanager.configuration.ConfigReader;

/**
 *
 * @author Silvan
 */
public class ClientSideWrapper {
    private static final String CONFIG_FILE = "config.xml";
    private static final Logger log = Logger.getLogger(ClientSideWrapper.class);
    private static ClientSideWrapper instance;    
    
    private ClientSideProxy proxy;

    public static void main(String argc[]) {
        try {
            ConfigReader confReader = ConfigReader.getInstance();
            confReader.readConfig(CONFIG_FILE);
            log.debug("Config readed.");
            
            instance = new ClientSideWrapper(confReader);
            
        } catch (IOException ex) {
            log.fatal("IO Error", ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "IO Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (BadConfigException ex) {
            log.fatal("Configuration Error", ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Configuration Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    private ClientSideWrapper(ConfigReader configReader) throws IOException {
            proxy = new ClientSideProxy(configReader);
    }
}
