package ua.group42.taskmanager.control.data;

import java.util.Collection;
import ua.group42.taskmanager.model.Task;

/**
 * Interface of Data Access Object
 * @author Group42
 */
public interface TaskDAO {    
    
    Collection<Task> loadTasks();
    
    void saveTask(Task task);
    
    void saveAllTasks(Collection<Task> tasks);
}
