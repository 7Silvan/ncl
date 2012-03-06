package ua.group42.taskmanager.server;

import java.util.Collection;

/**
 *
 * @author Silvan
 */
public interface ServerSideWrapperIface {
    // if not successfull serverSide calls showError of ServerGUI Iface
    public void banUser(String name);
    public void unBanUser(String name);
    public void regUser(String name);
    public void delUser(String name);
    public boolean canConnect(String name) throws IllegalAccessException;
    public UserModel getUser(String name) throws IllegalAccessException;
    public Collection getAllUsers();
    public Collection getActiveUsers();
    
    public int getWorkPort();
    public int getStopPort();
}
