package ua.group42.taskmanager.server;

import ua.group42.taskmanager.Notifiable;
import ua.group42.taskmanager.Updatable;
import ua.group42.taskmanager.model.Task;

/**
 *
 * @author Silvan
 */
public class ServerSideProxy implements Updatable, Notifiable{

    private ServWorker worker = null;
    
    public ServerSideProxy(ServWorker worker) {
        this.worker = worker;
    }
    
    @Override
    public void update() {
        worker.update();
    }

    @Override
    public void taskNotify(Task task) {
        worker.taskNotify(task);
    }
    
}