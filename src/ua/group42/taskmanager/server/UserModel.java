package ua.group42.taskmanager.server;

/**
 *
 * @author Silvan
 */
public class UserModel {

    public enum State {

        ONLINE, OFFLINE, BANNED
    }
    private String name;
    private State state;  //wheter state is online or offline or banned;

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
}