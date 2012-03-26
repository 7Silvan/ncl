package ua.group42.taskmanager.protocol;

import java.text.ParseException;
import org.apache.log4j.Logger;

/**
 *
 * @author Silvan
 */
public class RequestParser {
    
    private static final Logger log = Logger.getLogger(RequestParser.class);
    
        private MessageParser msg;
        
        private String methodName;
        private Object parametr;
        
        public RequestParser(MessageParser msg) throws ParseException {
            this.msg = msg;
            parse();
        }
        
        private void parse() throws ParseException {
            methodName = msg.getDoc().getRootElement().getChildText("methodName");
            if (methodName.equalsIgnoreCase("logIn") ||
                    methodName.equalsIgnoreCase("logOut") ||
                    methodName.equalsIgnoreCase("removeTask"))
                parametr = msg.getDoc().getRootElement().getChild("params")
                        .getChild("param").getChild("value").getChildText("string");
            
            if (methodName.equalsIgnoreCase("addTask") ||
                    methodName.equalsIgnoreCase("editTask") ||
                    methodName.equalsIgnoreCase("taskNotify"))
                parametr = NetProtocol.structToTask(msg.getDoc().getRootElement().getChild("params")
                        .getChild("param").getChild("value").getChild("struct"));
        }

        public String getMethodName() {
            return methodName;
        }

        public Object getParametr() {
//            if (parametr == null) { 
//                log.error("Access denied. Object doesn't exist.");
//                throw new IllegalAccessError("Access denied. Object doesn't exist.");
//            }
            return parametr;
        }
        
    }
    