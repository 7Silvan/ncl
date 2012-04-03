package ua.group42.taskmanager.common.tools;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.apache.log4j.*;

/**
 * This class calls for driving errors when validating or parsing xml
 * @author Group42
 */
class SimpleErrorHandler implements ErrorHandler {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(SimpleErrorHandler.class);

    
    @Override
    public void warning(SAXParseException exception) {
        log.error(formatError(exception), exception);
    }

    @Override
    public void error(SAXParseException exception) {
        log.error(formatError(exception), exception);
    }

    @Override
    public void fatalError(SAXParseException exception) {
        log.error(formatError(exception), exception);
    }

    /**
     * This method formats catched error due to showing cause
     * of error and line of it's location in the xml files
     * @param e  is the cathced exception
     * @return string with info about cause and location
     */
    private String formatError(SAXParseException e) {
        return new StringBuilder(e.getMessage()).append(" Line #").append(e.getLineNumber()).toString();
    }
}
