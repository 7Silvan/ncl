package ua.group42.taskmanager.control.data;

import java.io.IOException;
import java.util.Collection;
import ua.group42.taskmanager.model.Task;

/**
 * Interface of Data Access Object
 * @author Group42
 */
public interface TaskDAO {    
    
    Collection<Task> loadTasks() throws IOException;
    
    void saveTask(Task task) throws IOException;
    
    void saveAllTasks(Collection<Task> tasks) throws IOException;
}
