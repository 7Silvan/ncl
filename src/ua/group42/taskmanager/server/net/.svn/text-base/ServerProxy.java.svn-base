package ua.group42.taskmanager.server.net;

import ua.group42.taskmanager.common.Notifiable;
import ua.group42.taskmanager.common.Updatable;
import ua.group42.taskmanager.common.model.Task;

/**
 *
 * @author Silvan
 */
public class ServerProxy implements Updatable, Notifiable{

    private ServWorker worker = null;
    
    public ServerProxy(ServWorker worker) {
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