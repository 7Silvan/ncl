package ua.group42.taskmanager.common.view;

import ua.group42.taskmanager.common.Notifiable;
import ua.group42.taskmanager.common.Updatable;
import ua.group42.taskmanager.common.control.ControllerIface;

public interface MainView extends Notifiable, Updatable {
    public boolean regController(ControllerIface control);
    public void closeView(TaskView view);
    public ControllerIface getControl();
    
    public void showAll();
    public void hideAll();
}