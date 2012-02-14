package ua.group42.taskmanager.control.data;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import ua.group42.taskmanager.control.ConfigReader;
import org.apache.log4j.*;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import ua.group42.taskmanager.model.InternalControllerException;
import ua.group42.taskmanager.model.Task;
import ua.group42.taskmanager.model.TaskComparator;
import ua.group42.taskmanager.tools.*;

/**
 * Operates with Data through the XML format.
 * 
 * @author Group42
 */
public final class XmlDAO implements TaskDAO {

    private static final Logger log = Logger.getLogger(XmlDAO.class);
    private final TaskComparator taskComparator = new TaskComparator();
    private ConfigReader config;
    private Document doc;

    public XmlDAO(ConfigReader confReader) throws IOException, InvalidFileException {
        config = confReader;
    }

    @Override
    public void saveTask(Task task) {

        try {
        if (doc == null) {
            doc = new Document(new Element("tasks"), new DocType("tasks", "XmlDTD.dtd"));
        }
        
         doc.getRootElement().addContent(new Element("task")
                    .addContent(new Element("name").addContent(task.getName()))
                    .addContent(new Element("description").addContent(task.getDescription()))
                    .addContent(new Element("contacts").addContent(task.getContacts()))
                    .addContent(new Element("date").addContent(task.getStringDate()))
         );
         
         XMLOutputter outPutter = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n"));
         
          outPutter.output(doc, new FileWriter(config.getFileName()));

        } catch (IOException ex) {
            log.error(null, ex);
            throw new WritingFileException("Didn't write xml", ex.getCause());
        } 
    }

    @Override
    public  void saveAllTasks(Collection<Task> tasks) {

        try {
                doc = new Document(new Element("tasks"), new DocType("tasks", "XmlDTD.dtd"));

        Iterator it = tasks.iterator();
        while (it.hasNext()) {
            Task task = (Task) it.next();
            doc.getRootElement().addContent(new Element("task")
                    .addContent(new Element("name").addContent(task.getName()))
                    .addContent(new Element("description").addContent(task.getDescription()))
                    .addContent(new Element("contacts").addContent(task.getContacts()))
                    .addContent(new Element("date").addContent(task.getStringDate()))
                    );
        }
        
       XMLOutputter outPutter = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n"));
         
          outPutter.output(doc, new FileWriter(config.getFileName()));

        } catch (IOException ex) {
            log.error(null, ex);
            throw new WritingFileException("Didn't write xml", ex.getCause());
        } 
    }

    @Override
    public Collection<Task> loadTasks()  {
        Collection<Task> loadedTasks = null;
        try {
        Boolean valid = Tools.valXML(config.getFileName());
        if (valid) {
            try {
                SAXBuilder builder = new SAXBuilder();
                doc = builder.build(config.getFileName());

                Iterator it = doc.getRootElement().getChildren("task").iterator();

                loadedTasks = new TreeSet<Task>(taskComparator);

                while (it.hasNext()) {
                    Element element = (Element) it.next();

                    String name = element.getChildText("name");
                    String description = element.getChildText("description");
                    String contact = element.getChildText("contacts");
                    String date = element.getChildText("date");

                    SimpleDateFormat sdf = new SimpleDateFormat(ConfigReader.getInstance().getDateFormat());


                    Task task = new Task(name, description, contact, sdf.parse(date));

                    loadedTasks.add(task);
                }
            } catch (JDOMException ex) {
                log.error("Error in loading tasks from xml ", ex);
                throw new IOException("Error in loading tasks from xml ", ex);
            } catch (ParseException ex) {
                log.error("Error in xml parsing.");
                throw new IOException("Error in xml parsing.", ex);
            }
        } else {
            log.info("XML File is not valid. Check it.");
            throw new InvalidFileException("XML File is not valid. Check it.");
        }
        } catch (Exception ex) {
            log.error("Tasks wasn't loaded, IO or XML errors occured",ex);
            throw new InternalControllerException("Tasks wasn't loaded, IO or XML errors occured",ex);
        }
        return loadedTasks;
    }
}
