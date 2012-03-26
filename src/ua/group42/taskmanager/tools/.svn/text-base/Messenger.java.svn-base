package ua.group42.taskmanager.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.text.ParseException;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import ua.group42.taskmanager.control.InternalControllerException;
import ua.group42.taskmanager.protocol.MessageParser;
import ua.group42.taskmanager.protocol.ResponseParser;

/**
 *
 * @author Silvan
 */
public class Messenger {

    private static final Logger log = Logger.getLogger(Messenger.class);
    private ObjectInputStream reader = null;
    private ObjectOutputStream writer = null;
    private Socket connection = null;
    private volatile Integer ownerQueue = 0;
    private volatile Integer ownerRequests = 0;
    private volatile String threadName = null;

    public Messenger(Socket connection) throws IOException {
        this.connection = connection;
        writer = new ObjectOutputStream(connection.getOutputStream());
        reader = new ObjectInputStream(connection.getInputStream());
    }

    public Object read() throws IOException, ClassNotFoundException {
        return reader.readObject();
    }

    public void write(Object o) throws IOException {
        if (connection.isConnected() && !connection.isOutputShutdown()) {
            if (o != null) {
                writer.writeObject(o);
            } else {
                log.error("Warning, null-reference passed into write method"); // FIXME: crutch
            }
        } else {
            log.error("Connection or IO streams isn't ready");
            throw new IOException("Connection or IO streams isn't ready");
        }
    }

    private String readMessage() throws IOException, ClassNotFoundException {

        String msg = null;

        log.info("Scanning message");
        msg = (String) reader.readObject();

        if (msg == null || msg.equals("")) {
            log.error("nothing came");
            throw new IOException("Readed null or nothing.");// FIXME : write your own exception for such situations
        }

        return msg;
    }

    /**
     * Reads string message by calling method @see readMessage() and executes 1st level parsing of message,
     * by protocol, every message is xml-message with request or response
     * @return new MessageParser object with readed message from connection input stream
     * @throws JDOMException throws when have troubles with 1st level parsing 
     * @throws IOException throws when have troubles with reading message from input stream
     * @throws ClassNotFoundException throws when "Dunno what a crap is that 0_o!" 
     */
    public MessageParser getNextMessage() throws IOException {
        try {
            String message = readMessage();

            log.info(" Gotcha message: \n" + message);

            SAXBuilder builder = new SAXBuilder();
            return new MessageParser(builder.build(new StringReader(message)));
        } catch (ClassNotFoundException impossible) {
            log.error("impossible error happened", impossible);
            throw new InternalControllerException("impossible error happened", impossible);
        } catch (JDOMException ex) {
            log.error("Parsing message error.", ex);
            throw new InternalControllerException("Parsing message error.", ex);
        }
    }

    public ResponseParser getNextResponse() throws IOException {
        try {
            MessageParser msg = getNextMessage();

            if (msg.isResponse()) {
                return new ResponseParser(msg);
            } else {
                log.error("Unrelative message got.");
                throw new InternalControllerException("Unrelative message got.");
                // careful, runtime exception
            }
        } catch (ParseException ex) {
            log.error("Parsing message error.", ex);
            throw new InternalControllerException("Parsing message error.", ex);
        }
    }

    public synchronized void checkOwners() {
        ownerRequests++;
        log.debug("Checking Queue +1 ( " + ownerRequests + " req-s): " + ownerQueue + " by " + Thread.currentThread().getName());
        if (ownerQueue > 0 && ((threadName == null) ? false : !threadName.equals(Thread.currentThread().getName()))) {
            try {
                wait();
            } catch (InterruptedException ex) {
                log.error("Interrupted");
            }
        }
    }

    public synchronized void owned() {

        ownerQueue++;
        threadName = Thread.currentThread().getName();
        ownerRequests--;
        log.debug("Owned ( " + ownerRequests + " req-s): " + ownerQueue + " by " + Thread.currentThread().getName());
    }

    public void leaving() {
        ownerQueue--;
        log.debug("Leaved ( " + ownerRequests + " req-s): " + ownerQueue + " by " + Thread.currentThread().getName());
        notifyAll();
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
        if (writer != null) {
            writer.close();
        }
    }
}
