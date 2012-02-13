package ua.group42.taskmanager.view;

import ua.group42.taskmanager.Notifiable;
import ua.group42.taskmanager.Updatable;
import ua.group42.taskmanager.control.ControllerIface;

public interface MainView extends Notifiable, Updatable {
    public boolean regController(ControllerIface control);
    public void closeView(TaskView view);
    public ControllerIface getControl();
    
    public void showAll();
    public void hideAll();
}