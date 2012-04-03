package ua.group42.taskmanager.server.model;

import ua.group42.taskmanager.server.net.ServWorker;
import ua.group42.taskmanager.server.model.State;

/**
 *
 * @author Silvan
 */
public class UserModel {

    private String name;
    private State state;  //wheter state is online or offline or banned;
    private ServWorker worker;

    public UserModel(String name) {
        this.name = name;
        setState(State.OFFLINE);
    }

    public String getName() {
        return name;
    }

    public State getState() {
        return state;
    }

    public void setState(State status) {
        this.state = status;
    }
    
    public boolean isBanned() {
        return (state == State.BANNED);
    }

    public ServWorker getWorker() {
        return worker;
    }

    public void setWorker(ServWorker worker) {
        this.worker = worker;
    }    
}