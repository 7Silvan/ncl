package ua.group42.taskmanager.control.data;

import ua.group42.taskmanager.control.InternalControllerException;

/**
 *
 * @author Group42
 */
public class WritingFileException extends InternalControllerException {

    public WritingFileException(String message) {
        super(message);
    }
    
    public WritingFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
