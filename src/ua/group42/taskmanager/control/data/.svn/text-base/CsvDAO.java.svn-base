package ua.group42.taskmanager.control.data;

import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import ua.group42.taskmanager.configuration.ConfigReader;
import org.apache.log4j.*;
import ua.group42.taskmanager.control.InternalControllerException;
import ua.group42.taskmanager.model.Task;
import ua.group42.taskmanager.model.TaskComparator;
import ua.group42.taskmanager.tools.CSVTable;

/**
 * Operates with Data through the XML format.
 * 
 * @author Group42
 */
public final class CsvDAO implements TaskDAO {

    private static final Logger log = Logger.getLogger(XmlDAO.class);
    private final TaskComparator taskComparator = new TaskComparator();
    private CSVTable table = null;
    private PrintStream out = null;
    private ConfigReader config;
    private SimpleDateFormat sdf = null;
    
    public CsvDAO(ConfigReader confReader) {
        config = confReader;
        sdf = new SimpleDateFormat(config.getDateFormat());
    }

    @Override
    public void saveTask(Task task) {

        try {

            int row = table.getRowCount();
            table.insertRow(row--);
            table.setValue(row, "id", task.getId());
            table.setValue(row, "name", task.getName());
            table.setValue(row, "description", task.getDescription());
            table.setValue(row, "date", sdf.format(task.getDate()));

            out = new PrintStream(config.getFileName());            
            table.writeTo(out);

        } catch (IOException ex) {
            log.error(null, ex);
            throw new WritingFileException("Didn't write xml", ex.getCause());
        } finally {
             try {
            if (out != null) {
                out.flush();
                out.close();
            }
            } catch (Exception ex) {
                log.error("Can't close stream of " + config.getFileName());
                throw new InternalControllerException("Closing File Error.", ex);
            }
        }
    }

    @Override
    public void saveAllTasks(Collection<Task> tasks) {
        try {

            table.eraseTable();

            Iterator it = tasks.iterator();
            while (it.hasNext()) {
                Task task = (Task) it.next();

                int row = table.getRowCount();
                table.insertRow(row--);
                table.setValue(row, "id", task.getId());
                table.setValue(row, "name", task.getName());
                table.setValue(row, "description", task.getDescription());
                table.setValue(row, "date", sdf.format(task.getDate()));

            }

            out = new PrintStream(config.getFileName());
            table.writeTo(out);

        } catch (IOException ex) {
            log.error(null, ex);
            throw new WritingFileException("Didn't write xml", ex.getCause());
        } finally {
            try {
            if (out != null) {
                out.flush();
                out.close();
            }
            } catch (Exception ex) {
                log.error("Can't close stream of " + config.getFileName());
                throw new InternalControllerException("Closing File Error.", ex);
            }
        }
    }

    @Override
    public Collection<Task> loadTasks() {
        
        Collection<Task> loadedTasks = null;

        try {
            table = new CSVTable(config.getFileName());

            loadedTasks = new TreeSet<Task>(taskComparator);

            for (int i = 0; i < table.getRowCount(); i++) {
                String id = table.getValue(i, "id");
                String name = table.getValue(i, "name");
                String description = table.getValue(i, "description");
                String contact = table.getValue(i, "contacts");
                String date = table.getValue(i, "date");

                Task task = new Task(id, name, description, sdf.parse(date));
                
                loadedTasks.add(task);
            }
        } catch (ParseException ex) {
            log.error("Error in data string parsing.");
            throw new InternalControllerException("Error in data string parsing.", ex);
        }
        return loadedTasks;
    }
}
