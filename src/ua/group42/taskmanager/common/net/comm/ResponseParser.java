package ua.group42.taskmanager.common.net.comm;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Element;
import ua.group42.taskmanager.common.model.Task;

/**
 *
 * @author Silvan
 */
public class ResponseParser {

    private static final Logger log = Logger.getLogger(ResponseParser.class);
    private MessageParser msg;
    private boolean isOk;
    private boolean hasFault;
    private boolean hasFaultArgument;
    private boolean hasTasks;
    private int faultCode;
    private String faultString;
    private String faultArgument;
    private Collection<Task> tasks = null;

    public ResponseParser(MessageParser msg) throws ParseException {
        this.msg = msg;
        parse();
    }

    private void parse() throws ParseException {
        if (msg.getDoc().getRootElement().getChild("fault") == null) {
            hasFault = false;
            Element value = msg.getDoc().getRootElement().getChild("params").getChild("param").getChild("value");
            if (value.getChild("boolean") != null) {
                isOk = Boolean.parseBoolean(value.getChildText("boolean"));
            }
            if (value.getChild("array") != null) {
                hasTasks = true;
                tasks = NetProtocol.getTasks(value.getChild("array"));
            }

        } else {
            hasFault = true;

            List<Element> members = msg.getDoc().getRootElement().getChild("fault").getChild("value").getChild("struct").getChildren("member");
            for (Element member : members) {
                if (member.getChildTextTrim("name").equalsIgnoreCase("faultCode")) {
                    faultCode = Integer.parseInt(member.getChild("value").getChildText("i4"));
                } else if (member.getChildTextTrim("name").equalsIgnoreCase("faultString")) {
                    faultString = member.getChild("value").getChildText("string");
                } else {
                    hasFaultArgument = true;
                    faultArgument = member.getChild("value").getChildText("string");
                }
            }
        }
    }

    public boolean hasFaultArgument() {
        return hasFaultArgument;
    }

    public String getFaultArgument() {
        if (!hasFaultArgument) {
            log.error("Access denied. Object doesn't exist.");
            throw new IllegalAccessError("Access denied. Object doesn't exist.");
        }
        return faultArgument;
    }

    public int getFaultCode() {
        if (!hasFault) {
            log.error("Access denied. Object doesn't exist.");
            throw new IllegalAccessError("Access denied. Object doesn't exist.");
        }
        return faultCode;
    }

    public String getFaultString() {
        if (!hasFault) {
            log.error("Access denied. Object doesn't exist.");
            throw new IllegalAccessError("Access denied. Object doesn't exist.");
        }
        return faultString;
    }

    public boolean hasFault() {
        return hasFault;
    }

    public boolean isOk() {
        return isOk;
    }

    public boolean hasTasks() {
        return hasTasks;
    }

    public Collection<Task> getTasks() {
        if (!hasTasks) {
            log.error("Access denied. Object doesn't exist.");
            throw new IllegalAccessError("Access denied. Object doesn't exist.");
        }
        return tasks;
    }
}