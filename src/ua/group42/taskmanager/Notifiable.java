package ua.group42.taskmanager;

import ua.group42.taskmanager.model.Task;

/**
 *
 * @author Group42
 */
public interface Notifiable extends Listener{
    void taskNotify(Task task);
}