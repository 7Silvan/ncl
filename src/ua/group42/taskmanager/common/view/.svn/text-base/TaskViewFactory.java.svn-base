package ua.group42.taskmanager.common.view;

import org.apache.log4j.Logger;
import ua.group42.taskmanager.common.control.ControllerIface;
import ua.group42.taskmanager.common.model.Task;

/**
 * This class responses for creating and managing view's
 * (Singleton Factory Method)
 * 
 * @author Group42
 */
public class TaskViewFactory {

    public static final Logger log = Logger.getLogger(TaskViewFactory.class);

    public static TaskView init(ControllerIface control) {
        MainView view = (MainView) createView(TypeView.main, null);
        log.debug("Created main view.");
        view.regController(control);
        log.debug("Reggistered given controller.");
        view.update();
        log.debug("Updated main view.");
        return (TaskView)view;
    }
    
    public static TaskView createView(TypeView type, Task task) {

        switch (type) {
            case main: {
                TaskView newView = TaskSwingView.getInstance();
                log.debug("Passing link on TaskSwingView.");
                return newView;
            }
            case add: {
                TaskSwingAddView newView = new TaskSwingAddView();
                log.debug("Creating and passing link on TaskSwingAddView.");
                return newView;
            }
            case edit: {
                if (task == null) {
                    throw new NullPointerException("EditView needs task to view.");
                } else {
                    TaskView newView = new TaskSwingEditView(task);
                    log.debug("Creating and passing link on TaskSwingEditView.");
                    return newView;
                }
            }
            case notify: {
                if (task == null) {
                    throw new NullPointerException("NotifyView needs task to view.");
                } else {
                    TaskView newView = new TaskSwingNotifyView(task);
                    log.debug("Creating and passing link on TaskSwingNotifyView.");
                    return newView;
                }
            }
            default:
                log.error(String.format("This type [\"%s\"] is not accessable.", type.name()));
        }

        return null;
    }
}
