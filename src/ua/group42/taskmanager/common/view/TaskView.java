package ua.group42.taskmanager.common.view;
/**
 *
 * @author Group42
 * 
 */
public interface TaskView {
    TypeView getType();
    void show();
    void hide();
    void close();
}