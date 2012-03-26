package ua.group42.taskmanager.control;

import ua.group42.taskmanager.model.InvalidTaskException;
import java.util.Collection;
import ua.group42.taskmanager.Listener;
import ua.group42.taskmanager.configuration.ConfigReader;
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
    public void addTask(Task task) throws InvalidTaskException, InternalControllerException;

    /**
     * Method for adding task from view in raw form (with parametrs of task)
     * and validating for notNullness and date correctness
     * @param name
     * @param description
     * @param contacts
     * @param date
     * @throws InvalidTaskException throws if task didn't pass validation
     */
    public void addTask(String name, String description, String date) throws InvalidTaskException, InternalControllerException;
    
    public void addTask(String id, String name, String description, String date) throws InvalidTaskException, InternalControllerException;

    public void addPostponedTask(Task task) throws InvalidTaskException, InternalControllerException;

    public void removeTask(Task task) throws InternalControllerException;

    public void removeTask(String id) throws InternalControllerException;

    public void editTask(Task was, Task become) throws InvalidTaskException, InternalControllerException;

    public void editTask(String idTaskWas, Task become) throws InvalidTaskException, InternalControllerException;

    public void editTask(String idWas, String newName, String newDescription, String newDate) throws InvalidTaskException, InternalControllerException;

    /**
     * Can throw InternalControllerException extended from RuntimeEx.
     * @return Collection of tasks
     */
    public Collection<Task> getTasks();

    public void addListener(Listener listener);

    public void removeListener(Listener listener);

    public String getDateFormat();
    
    public ConfigReader getConfigReader();

    public void stopService();
}