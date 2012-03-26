package ua.group42.taskmanager.server;

import java.util.Collection;
import ua.group42.taskmanager.configuration.ConfigReader;

/**
 *
 * @author Silvan
 */
public interface ServerSideWrapperIface {
    
    // if not successfull serverSide calls showError of ServerGUI Iface from
    // Exception catchers
    
    public void banUser(String name)throws IllegalAccessException;
    public void unBanUser(String name)throws IllegalAccessException;
    public void regUser(String name)throws IllegalAccessException;
    public void delUser(String name)throws IllegalAccessException;
    
    public Boolean canConnect(String name) throws IllegalAccessException;
    public Boolean canDisConnect(String name) throws IllegalAccessException;
    public Boolean doConnect(String name) throws IllegalAccessException;
    public Boolean doDisConnect(String name) throws IllegalAccessException;
    
    public Boolean isUserOnline(String name) throws IllegalAccessException;
    public Boolean isUserBanned(String name) throws IllegalAccessException;
    
    public Collection getAllUsers();
    public Collection getActiveUsers();
    
    public int getWorkPort();
    public int getStopPort();

    public ConfigReader getConfig();

    public void stopServer();
    
    public void secret();
}
