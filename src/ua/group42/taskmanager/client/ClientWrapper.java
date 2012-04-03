package ua.group42.taskmanager.client;

import java.io.IOException;
import javax.swing.JOptionPane;
import org.apache.log4j.*;
import ua.group42.taskmanager.common.configuration.BadConfigException;
import ua.group42.taskmanager.common.configuration.ConfigReader;
import ua.group42.taskmanager.common.control.ControllerIface;
import ua.group42.taskmanager.common.view.MainView;
import ua.group42.taskmanager.common.view.SysTray;
import ua.group42.taskmanager.common.view.TaskView;
import ua.group42.taskmanager.common.view.TaskViewFactory;

/**
 *
 * @author Silvan
 */
public class ClientWrapper {

    private static final String CONFIG_FILE = ".\\dist-client\\config.xml";
    private static final Logger log = Logger.getLogger(ClientWrapper.class);
    private ClientProxy proxy;
    private TaskView mainView;

    public static void main(String argc[]) {

        final ClientWrapper instance;

        try {
            ConfigReader confReader = ConfigReader.getInstance();
            confReader.readConfig(CONFIG_FILE);
            log.debug("Config readed.");

            instance = new ClientWrapper(confReader);

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

    private ClientWrapper(ConfigReader configReader) throws IOException {
        proxy = new ClientProxy(configReader);
    }
}