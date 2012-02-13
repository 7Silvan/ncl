package ua.group42.taskmanager;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import ua.group42.taskmanager.control.BadConfigException;
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
    /**
     * List for listeners that will listen for events, Conquer and Command ))
     */
    private Collection listeners = new LinkedList<Listener>();

    public static void main(String[] args) throws BadConfigException {

        starter = new StartWrapper();

        // Start Controller
        final ControllerIface controller = (ControllerIface) TaskController.getInstance();
        controller.addListener( (Listener) starter);

//        final TaskView mainView = starter.createView(TypeView.main);
//        starter.addListener(mainView);
        final TaskView mainView = TaskViewFactory.init(controller);
        starter.addListener((MainView) mainView);

        new Runnable() {

            @Override
            public void run() {
                try {
                    starter.initSysTray((MainView)mainView);
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
        //addListener(new SysTray());
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
