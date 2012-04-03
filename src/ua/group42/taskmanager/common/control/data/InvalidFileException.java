package ua.group42.taskmanager.common.control.data;

import java.io.IOException;

/**
 *
 * @author Silvan
 */
public class InvalidFileException extends IOException {

    public InvalidFileException(String msg) {
        super(msg);
    }
    
    public InvalidFileException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    public InvalidFileException(Throwable cause) {
        super(cause);
    }
}
