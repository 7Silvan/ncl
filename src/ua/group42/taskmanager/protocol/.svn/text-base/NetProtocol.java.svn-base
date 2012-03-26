package ua.group42.taskmanager.protocol;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import ua.group42.taskmanager.configuration.ConfigReader;
import ua.group42.taskmanager.model.Task;

/**
 * Serves building and parsing requests and responses
 * @author Silvan
 */
public class NetProtocol {
    
    private static final Logger log = Logger.getLogger(NetProtocol.class);
    
    public static int InvalidParametrError = 1;
    public static int CannotProvideActionError = 2;
    public static int WrongLoginNameError = 3;
    public static int ServerStoppedError = 4;
    public static int WrongParametrsError = 5;
    
    public enum Type {
        BOOLEAN {
            @Override
            public String toString() {
                return "boolean";
            }
        }, INTEGER {
            @Override
            public String toString() {
                return "i4";
            }
        }, STRING {
            @Override
            public String toString() {
                return "string";
            }
        }
    }
    
    public static String nameOfError(Integer numOfError) {
        switch (numOfError) {
            case 1: return "Invalid Parametr";
            case 2: return "Cannot provide action";
            case 3: return "Wrong Login name";
            case 4: return "Server Stopped";
            case 5: return "Too many parametrs";
            default: throw new RuntimeException("Wrong Error Number");
        }
    }
    
    public static String nameOfErrorArgument(Integer numOfError) {
        switch (numOfError) {
            case 1: return "parametrName";
            case 2: return "askedAction";
            case 3: return "loginName";
            case 4: return "";
            case 5: return "";
            default: throw new RuntimeException("Wrong Error Number");
        }
    }
    
    public static Element newMemberOfStruct(String name, String value, Type type) {
        Element member = new Element("member");
        member.addContent(new Element("name").addContent(name));
        member.addContent(new Element("value").addContent(new Element(type.toString()).addContent(value)));
        return member; 
    }
    
    private static Element newTaskStruct(String id, String name, String description, String date) {
        Element struct = new Element("struct");

                    struct.addContent(newMemberOfStruct(
                        "id", 
                        id, 
                        Type.STRING));
                    struct.addContent(newMemberOfStruct(
                        "name", 
                        name, 
                        Type.STRING));
                    struct.addContent(newMemberOfStruct(
                        "description", 
                        description, 
                        Type.STRING));
                    struct.addContent(newMemberOfStruct(
                        "date", 
                        date, 
                        Type.STRING));
                    
          return struct;
    }
    
    public static Element newTaskStruct(Task task){
        return newTaskStruct(task.getId(), task.getName(), task.getDescription(), task.getStringDate());
    }
    
    /**
     * Extracts Collection of Tasks from array of structures in xml
     * @return Collection of Tasks
     * @throws ParseException if has problems with parsing
     */
    public static Collection<Task> getTasks(Element array) throws ParseException {
        Collection<Task> tasks = new ArrayList();
        List<Element> taskValuesStructs = array.getChild("data").getChildren("value");

        Iterator<Element> it = taskValuesStructs.iterator();
        while (it.hasNext()) {
            tasks.add(structToTask(it.next().getChild("struct")));
        }
        return tasks;
    }

    public static Task structToTask(Element taskStruct) throws ParseException {
        String id = null;
        String name = null;
        String desription = null;
        Date date = null;

        SimpleDateFormat sdf = new SimpleDateFormat(ConfigReader.getInstance().getDateFormat());

        List<Element> taskList = taskStruct.getChildren("member");
        Iterator<Element> members = taskList.iterator();
        while (members.hasNext()) {
            Element member = members.next();
            if (member.getChildText("name").equalsIgnoreCase("id")) {
                id = member.getChild("value").getChildText("string");
            }
            if (member.getChildText("name").equalsIgnoreCase("name")) {
                name = member.getChild("value").getChildText("string");
            }
            if (member.getChildText("name").equalsIgnoreCase("description")) {
                desription = member.getChild("value").getChildText("string");
            }
            if (member.getChildText("name").equalsIgnoreCase("date")) {
                    date = sdf.parse(member.getChild("value").getChildText("string"));
            }
        }
            return new Task(id, name, desription, date);
     }


    public static void main(String[] args) {
        //System.out.println(NetProtocol.ServerSide.responseError(1,"Yes"));
        Collection<Task> tasks = new ArrayList<Task>();
        tasks.add(new Task("1","1","1",new Date()));
        tasks.add(new Task("2","2","2",new Date()));
        System.out.println(NetProtocol.ServerSide.responseTransferTasks(tasks));
        System.out.println(NetProtocol.ServerSide.requestServStopped("blablabla"));
        System.out.println(NetProtocol.ServerSide.requestTaskNotify(new Task(null,"2","2",new Date())));
    }
    
    public static class ServerSide {

        private static final Logger log = Logger.getLogger(ServerSide.class);
        
        public static String responseOK() {
            StringWriter response = null;
            try {
                response = new StringWriter();
                Document doc = new Document(new Element("methodResponse"));
                doc.getRootElement()
                        .addContent(new Element("params")
                        .addContent(new Element("param")
                        .addContent(new Element("value")
                        .addContent(new Element("boolean")
                        .addContent("true")))));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, response);
                return response.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
//        return "Internal Server Error 500";  // ))
        }

        public static String responseError(Integer NumOfError, String concrete) {
             StringWriter response = null;
            try {
                response = new StringWriter();
                Document doc = new Document(new Element("methodResponse"));
                
                Element struct = new Element("struct");

                struct.addContent(newMemberOfStruct(
                        "faultCode", 
                        NumOfError.toString(), 
                        Type.INTEGER));
                
                        struct.addContent(newMemberOfStruct(
                        "faultString", 
                        nameOfError(NumOfError), 
                        Type.STRING));
                        
                        if (concrete != null && !nameOfErrorArgument(NumOfError).equals(""))  {
                            struct.addContent(newMemberOfStruct(
                            nameOfErrorArgument(NumOfError), 
                            concrete, 
                            Type.STRING));
                        
                        }
                
                doc.getRootElement()
                        .addContent(new Element("fault")
                        .addContent(new Element("value")
                        .addContent(struct)
                        ));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, response);
                return response.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }
        
        public static String responseTransferTasks(Collection<Task> tasks) {
             StringWriter response = null;
            try {
                response = new StringWriter();
                Document doc = new Document(new Element("methodResponse"));
                
                Element data = new Element("data");
                
                Iterator<Task> it = tasks.iterator();
                while(it.hasNext()) {
                    Task task = it.next();
                    
                    Element struct = newTaskStruct(
                            task.getId(), 
                            task.getName(), 
                            task.getDescription(), 
                            task.getStringDate());
                    
                    data.addContent(new Element("value").addContent(struct));
                }
                
                doc.getRootElement()
                        .addContent(new Element("params")
                        .addContent(new Element("param")
                        .addContent(new Element("value")
                        .addContent(new Element("array")
                        .addContent(data)))));
                
                
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, response);
                return response.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }
        
        public static String requestUpdate() {
            StringWriter result = null;
            try {
                result = new StringWriter();
                Document doc = new Document(new Element("methodCall"));
                doc.getRootElement()
                        .addContent(new Element("methodName").addContent("update"))
                        .addContent(new Element("params"));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, result);
                return result.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }
        
        public static String requestServStopped(String reason) {
            StringWriter result = null;
            try {
                result = new StringWriter();
                Document doc = new Document(new Element("methodCall"));
                doc.getRootElement()
                        .addContent(new Element("methodName").addContent("serviceStoppedNotify"))
                        .addContent(new Element("params")
                        .addContent(new Element("param")
                        .addContent(new Element("value")
                        .addContent(new Element("string")
                        .addContent(reason)))));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, result);
                return result.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }
        
        public static String requestClientBanned() {
            StringWriter result = null;
            try {
                result = new StringWriter();
                Document doc = new Document(new Element("methodCall"));
                doc.getRootElement()
                        .addContent(new Element("methodName").addContent("banNotify"))
                        .addContent(new Element("params")
                        .addContent(new Element("param")
                        .addContent(new Element("value")
                        .addContent(new Element("boolean")
                        .addContent("true")))));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, result);
                return result.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }
        
        public static String requestTaskNotify(Task task) {
            StringWriter result = null;
            try {
                result = new StringWriter();
                Document doc = new Document(new Element("methodCall"));
                
                doc.getRootElement()
                        .addContent(new Element("methodName").addContent("taskNotify"))
                        .addContent(new Element("params")
                        .addContent(new Element("param")
                        .addContent(new Element("value")
                        .addContent(newTaskStruct(task))
                        )));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, result);
                return result.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }
    }
    
    public static class ClientSide {
        
        private static final Logger log = Logger.getLogger(ClientSide.class);
        
         public static String responseOK() {
            StringWriter response = null;
            try {
                response = new StringWriter();
                Document doc = new Document(new Element("methodResponse"));
                doc.getRootElement()
                        .addContent(new Element("params")
                        .addContent(new Element("param")
                        .addContent(new Element("value")
                        .addContent(new Element("boolean")
                        .addContent("true")))));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, response);
                return response.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }
         
         public static String responseError() {
            StringWriter response = null;
            try {
                response = new StringWriter();
                Document doc = new Document(new Element("methodResponse"));
                doc.getRootElement()
                        .addContent(new Element("params")
                        .addContent(new Element("param")
                        .addContent(new Element("value")
                        .addContent(new Element("boolean")
                        .addContent("true")))));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, response);
                return response.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }
         
         
         public static String requestTaskAdd(Task task) {
            StringWriter result = null;
            try {
                result = new StringWriter();
                Document doc = new Document(new Element("methodCall"));
                
                doc.getRootElement()
                        .addContent(new Element("methodName").addContent("addTask"))
                        .addContent(new Element("params")
                        .addContent(new Element("param")
                        .addContent(new Element("value")
                        .addContent(newTaskStruct(task))
                        )));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, result);
                return result.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }
         
         /**
          * In this case task.id is id of task which to edit, end other data is update
          * @param task data for update
          * @return xml-formed request
          */
         public static String requestTaskEdit(Task task) {
            StringWriter result = null;
            try {
                result = new StringWriter();
                Document doc = new Document(new Element("methodCall"));
                
                doc.getRootElement()
                        .addContent(new Element("methodName").addContent("editTask"))
                        .addContent(new Element("params")
                        .addContent(new Element("param")
                        .addContent(new Element("value")
                        .addContent(newTaskStruct(task))
                        )));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, result);
                return result.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }

         
         public static String requestTaskRemove(String taskId) {
            StringWriter result = null;
            try {
                result = new StringWriter();
                Document doc = new Document(new Element("methodCall"));
                
                doc.getRootElement()
                        .addContent(new Element("methodName").addContent("removeTask"))
                        .addContent(new Element("params")
                        .addContent(new Element("param")
                        .addContent(new Element("value")
                        .addContent(new Element("string").addContent(taskId)
                        ))));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, result);
                return result.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }

        public static String requestGetTasks() {
            StringWriter result = null;
            try {
                result = new StringWriter();
                Document doc = new Document(new Element("methodCall"));
                
                doc.getRootElement()
                        .addContent(new Element("methodName").addContent("getTasks"))
                        .addContent(new Element("params"));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, result);
                return result.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }

         
         public static String requestLogIn(String login) {
            StringWriter result = null;
            try {
                result = new StringWriter();
                Document doc = new Document(new Element("methodCall"));
                
                doc.getRootElement()
                        .addContent(new Element("methodName").addContent("logIn"))
                        .addContent(new Element("params")
                        .addContent(new Element("param")
                        .addContent(new Element("value")
                        .addContent(new Element("string").addContent(login)
                        ))));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, result);
                return result.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }
         
         public static String requestLogOut(String login) {
            StringWriter result = null;
            try {
                result = new StringWriter();
                Document doc = new Document(new Element("methodCall"));
                
                doc.getRootElement()
                        .addContent(new Element("methodName").addContent("logOut"))
                        .addContent(new Element("params")
                        .addContent(new Element("param")
                        .addContent(new Element("value")
                        .addContent(new Element("string").addContent(login)
                        ))));
                XMLOutputter output = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n\r"));
                output.output(doc, result);
                return result.toString();
            } catch (IOException ex) {
                log.error("Response building error", ex);
                throw new RuntimeException("Response building error", ex);
            }
        }
         
    }
}
