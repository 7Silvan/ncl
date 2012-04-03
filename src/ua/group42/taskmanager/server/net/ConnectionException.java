package ua.group42.taskmanager.server.net;

/**
 *
 * @author Silvan
 */
public class ConnectionException extends RuntimeException {

    /**
     * Creates a new instance of <code>ConnectionException</code> without detail message.
     */
    public ConnectionException() {
    }

    /**
     * Constructs an instance of <code>ConnectionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ConnectionException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>ConnectionException</code> with the specified detail message,
     * and cause of exception.
     * @param msg the detail message.
     * @param cause the cause of exception
     */
    public ConnectionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
