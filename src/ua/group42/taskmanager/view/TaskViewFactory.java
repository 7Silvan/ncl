package ua.group42.taskmanager.view;

import org.apache.log4j.Logger;
import ua.group42.taskmanager.control.ControllerIface;
import ua.group42.taskmanager.model.Task;

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
        view.regController(control);
        view.update();
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
