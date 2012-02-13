package ua.group42.taskmanager.control;

import java.text.SimpleDateFormat;
import ua.group42.taskmanager.Listener;
import ua.group42.taskmanager.model.Task;

/**
 *
 * @author Silvan
 */
public interface ControllerIface {

    /**
     * Method for adding task from view
     * @param task adding one
     */
    public void addTask(Task task) throws InvalidTaskException;
    
    /**
     * Method for adding task from view in raw form (with parametrs of task)
     * and validating for notNullness and date correctness
     * @param name
     * @param description
     * @param contacts
     * @param date
     * @throws InvalidTaskException throws if task didn't pass validation
     */
    public void addTask(String name, String description, String contacts, String date) throws InvalidTaskException;

    public void addPostponedTask(Task task) throws InvalidTaskException;

    public void removeTask(Task task);

    public void editTask(Task was, Task become) throws InvalidTaskException;

    public Task[] getTasks();

    public void addListener(Listener listener);
    
    public void removeListener(Listener listener);

    public SimpleDateFormat getDateFormatter();
    
}