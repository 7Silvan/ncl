package ua.group42.taskmanager.clientside;

import java.io.IOException;
import javax.swing.JOptionPane;
import org.apache.log4j.*;
import ua.group42.taskmanager.configuration.BadConfigException;
import ua.group42.taskmanager.configuration.ConfigReader;
import ua.group42.taskmanager.control.ControllerIface;
import ua.group42.taskmanager.view.MainView;
import ua.group42.taskmanager.view.SysTray;
import ua.group42.taskmanager.view.TaskView;
import ua.group42.taskmanager.view.TaskViewFactory;

/**
 *
 * @author Silvan
 */
public class ClientSideWrapper {

    private static final String CONFIG_FILE = "config.xml";
    private static final Logger log = Logger.getLogger(ClientSideWrapper.class);
    private ClientSideProxy proxy;
    private TaskView mainView;

    public static void main(String argc[]) {

        final ClientSideWrapper instance;

        try {
            ConfigReader confReader = ConfigReader.getInstance();
            confReader.readConfig(CONFIG_FILE);
            log.debug("Config readed.");

            instance = new ClientSideWrapper(confReader);

            Thread proxy = new Thread(instance.proxy);
            proxy.start();

            new Runnable() {

                @Override
                public void run() {
                    try {
                        instance.mainView = TaskViewFactory.init((ControllerIface) instance.proxy);
                        instance.proxy.addListener((MainView) instance.mainView);
                        
                        instance.mainView.show();
                        
                        instance.initSysTray((MainView) instance.mainView);
                        log.debug("SysTray started.");
                    } catch (Exception ex) {
                        log.error("SysTray didn't started.", ex);
                    }
                }
            }.run();

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

    /**
     * Initializing System Tray Menu
     */
    private void initSysTray(MainView view) {
        //addListener(new SysTray(view));
        SysTray sysTray = new SysTray(view);
    }

    private ClientSideWrapper(ConfigReader configReader) throws IOException {
        proxy = new ClientSideProxy(configReader);
    }
}