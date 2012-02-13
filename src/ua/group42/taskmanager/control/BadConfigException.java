package ua.group42.taskmanager.control;

/**
 *
 * @author Group42
 */
public class BadConfigException extends Exception {

    public BadConfigException(ConfigReader confReader) {
        super(confReader.toString());
    }

    public BadConfigException(String message) {
        super(message);
    }
    
    public BadConfigException(String message, Throwable cause) {
        super(message);
    }
}
