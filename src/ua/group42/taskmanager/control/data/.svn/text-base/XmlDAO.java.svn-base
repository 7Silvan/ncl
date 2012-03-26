package ua.group42.taskmanager.control.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import ua.group42.taskmanager.configuration.ConfigReader;
import org.apache.log4j.*;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import ua.group42.taskmanager.control.InternalControllerException;
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
    private SimpleDateFormat sdf = null;

    public XmlDAO(ConfigReader confReader) {
        config = confReader;
        sdf = new SimpleDateFormat(config.getDateFormat());
    }

    @Override
    public void saveTask(Task task) {

        try {
            if (doc == null) {
                doc = new Document(new Element("tasks"), new DocType("tasks", "XmlDTD.dtd"));
            }

            if (task != null) {
                doc.getRootElement()
                        .addContent(new Element("task").setAttribute("id", task.getId())
                        .addContent(new Element("name").addContent(task.getName()))
                        .addContent(new Element("description").addContent(task.getDescription()))
                        .addContent(new Element("date").addContent(sdf.format(task.getDate()))));
            }

            XMLOutputter outPutter = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n"));

            outPutter.output(doc, new FileWriter(config.getFileName()));

        } catch (IOException ex) {
            log.error(null, ex);
            throw new WritingFileException("Didn't write xml", ex.getCause());
        }
    }

    @Override
    public void saveAllTasks(Collection<Task> tasks) {

        try {
            doc = new Document(new Element("tasks"), new DocType("tasks", "XmlDTD.dtd"));

            Iterator it = tasks.iterator();
            while (it.hasNext()) {
                Task task = (Task) it.next();
                doc.getRootElement().addContent(new Element("task").setAttribute("id", task.getId()).addContent(new Element("name").addContent(task.getName())).addContent(new Element("description").addContent(task.getDescription())).addContent(new Element("date").addContent(sdf.format(task.getDate()))));
            }

            XMLOutputter outPutter = new XMLOutputter(Format.getRawFormat().setIndent(" ").setLineSeparator("\n"));

            outPutter.output(doc, new FileWriter(config.getFileName()));

        } catch (IOException ex) {
            log.error(null, ex);
            throw new WritingFileException("Didn't write xml", ex.getCause());
        }
    }

    @Override
    public Collection<Task> loadTasks() {
        Collection<Task> loadedTasks = null;
        try {
            String path = config.getFileName();
            if (new File(path).exists()) {
                Boolean valid = Tools.valXML(path);
                if (valid) {
                    try {
                        SAXBuilder builder = new SAXBuilder();
                        doc = builder.build(config.getFileName());

                        Iterator it = doc.getRootElement().getChildren("task").iterator();
                        loadedTasks = new TreeSet<Task>(taskComparator);

                        while (it.hasNext()) {
                            Element element = (Element) it.next();

                            String id = element.getAttributeValue("id");
                            String name = element.getChildText("name");
                            String description = element.getChildText("description");
                            String date = element.getChildText("date");

                            Task task = new Task(id, name, description, sdf.parse(date));

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
            } else {
                saveTask(null);
            }
        } catch (Exception ex) {
            log.error("Tasks wasn't loaded, IO or XML errors occured", ex);
            throw new InternalControllerException("Tasks wasn't loaded, IO or XML errors occured", ex);
        }
        return loadedTasks;
    }
}
