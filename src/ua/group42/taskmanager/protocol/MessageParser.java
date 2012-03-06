package ua.group42.taskmanager.protocol;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import ua.group42.taskmanager.model.Task;

/**
 *
 * @author Silvan
 */
  
    public class MessageParser {
        
        private Document doc;

        public Document getDoc() {
            return doc;
        }
        
        public MessageParser(Document doc) {
            this.doc = doc;
        }
        
        public boolean isRequest() {
            if (doc.getRootElement().getName().equalsIgnoreCase("methodCall")) 
                return true;
            else 
                return false;
        }
        
        public boolean isResponse() {
            if (doc.getRootElement().getName().equalsIgnoreCase("methodResponse")) 
                return true;
            else 
                return false;
        }
        
        private String getMethodCallName() {
            return doc.getRootElement().getChildTextTrim("methodName");
        }
        
        private Task getTask() throws ParseException {
            Element taskStruct = doc.getRootElement().getChild("params").getChild("param").getChild("value").getChild("struct");
            
            return NetProtocol.structToTask(taskStruct);
        }
        
        private Collection<Task> getTasks() throws ParseException {
            Collection<Task> tasks = new ArrayList();
            Element data = doc.getRootElement()
                    .getChild("params")
                    .getChild("param")
                    .getChild("value")
                    .getChild("array")
                    .getChild("data");
            List<Element> taskValuesStructs = data.getChildren("value");
            
            Iterator<Element> it = taskValuesStructs.iterator();
            while (it.hasNext()) {
                tasks.add(NetProtocol.structToTask(it.next().getChild("struct")));
            }
            return tasks;
        }
        
     }
