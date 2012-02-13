package ua.group42.taskmanager.tools;

/**
 *
 * @author Group42
 */
public class WritingFileException extends RuntimeException {

    public WritingFileException(String message) {
        super(message);
    }
    
    public WritingFileException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public WritingFileException(Throwable cause) {
        super(cause);
    }
}
