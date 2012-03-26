package ua.group42.taskmanager;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import ua.group42.taskmanager.configuration.BadConfigException;
import ua.group42.taskmanager.configuration.ConfigReader;
import ua.group42.taskmanager.control.ControllerIface;
import ua.group42.taskmanager.control.TaskController;
import ua.group42.taskmanager.model.Task;
import ua.group42.taskmanager.view.MainView;
import ua.group42.taskmanager.view.SysTray;
import ua.group42.taskmanager.view.TaskView;
import ua.group42.taskmanager.view.TaskViewFactory;

/**
 * This class is the Entry point, so it starts all necessary things for app.
 * @author Group42
 */
public class StartWrapper implements StartWrapperIface, Notifiable, Updatable {

    // I had nothing else to declare it static
    private static StartWrapper starter;
    private static final Logger log = Logger.getLogger(StartWrapper.class);
    private static final String CONFIG_FILE = "config.xml";
    /**
     * List for listeners that will listen for events, Conquer and Command ))
     */
    private Collection listeners = new LinkedList<Listener>();

    public static void main(String[] args) throws BadConfigException {

        starter = new StartWrapper();
        final ControllerIface controller;
        final TaskView mainView;

        ConfigReader confReader = ConfigReader.getInstance();
        //reading configuration
        try {
            confReader.readConfig(CONFIG_FILE);
            boolean matched = false;
            do {
                try {
                    String userName = JOptionPane.showInputDialog("Log in or press Cancel to halt", "singleUser");
                    if (userName == null) {
                        log.info("App will halt by user's request");
                        System.exit(0);
                    }
                    confReader.setUserName(userName);
                    matched = true;
                } catch (BadConfigException ex) {
                    log.error("userName didn't match", ex);
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "userName didn't match",
                            JOptionPane.ERROR_MESSAGE);
                }
            } while (!matched);
        } catch (BadConfigException ex) {
            log.fatal("Configuration creating process error", ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Configuration creating process error. App will halt", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (IOException ex) {
            log.fatal("IO Error", ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "IO Error with reading \"" + CONFIG_FILE + "\". App will halt", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Start Controller
        controller = (ControllerIface) new TaskController(confReader);
        controller.addListener((Listener) starter);

        mainView = TaskViewFactory.init(controller);
        starter.addListener((MainView) mainView);

        new Runnable() {

            @Override
            public void run() {
                try {
                    starter.initSysTray((MainView) mainView);
                    log.debug("SysTray started.");
                } catch (Exception ex) {
                    log.error("SysTray didn't started.", ex);
                }
            }
        }.run();

        mainView.show();
    }

    /**
     * Initializing System Tray Menu
     */
    private void initSysTray(MainView view) {
        //addListener(new SysTray(view));
        SysTray sysTray = new SysTray(view);
    }

    @Override
    public synchronized void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public synchronized void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void taskNotify(Task task) {
        for (Iterator it = listeners.iterator(); it.hasNext();) {
            Listener listener = (Listener) it.next();
            if (listener instanceof Notifiable) {
                ((Notifiable) listener).taskNotify(task);
            }
        }
    }

    @Override
    public void update() {
        for (Iterator it = listeners.iterator(); it.hasNext();) {
            Listener listener = (Listener) it.next();
            if (listener instanceof Updatable) {
                ((Updatable) listener).update();
            }
        }
    }
}
