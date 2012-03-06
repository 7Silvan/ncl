package ua.group42.taskmanager.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.*;
import ua.group42.taskmanager.configuration.ConfigReader;

/**
 *
 * @author Group42
 */
public final class Task implements Serializable, Comparable {

    private static final Logger log = Logger.getLogger(Task.class);
    private String id;
    private String name;
    private String description;
    private String contacts;
    private Date date;
    private TaskState state = TaskState.DEAD;

    /**
     * Constructs task with all given data
     * @param id is unique identificator in dao
     * @param name is title of task
     * @param description -\\-
     * @param contacts
     * @param date  is point in time when the task to launch
     */
    public Task(
            String id,
            String name,
            String description,
            Date date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        setAlive(); // why it asked to make class Task final?
    }

    /**
     * Most frequently calling by views, then id sets by controller
     */
    public Task(
            String name,
            String description,
            Date date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public Task(
            String id,
            String name,
            String description,
            String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(ConfigReader.getInstance().getDateFormat());
        this.id = id;
        this.name = name;
        this.description = description;
        try {
            this.date = sdf.parse(date);
        } catch (ParseException ex) {
            log.error("DateString didn't matched pattern: " + date);
            throw new ParseException("DateString didn't matched pattern: " + date, ex.getErrorOffset());
        }
        setAlive(); // why it asked to make class Task final?
    }

    public Task(
            String name,
            String description,
            String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(ConfigReader.getInstance().getDateFormat());

        this.name = name;
        this.description = description;
        try {
            this.date = sdf.parse(date);
        } catch (ParseException ex) {
            log.error("DateString didn't matched pattern: " + date);
            throw new ParseException("DateString didn't matched pattern: " + date, ex.getErrorOffset());
        }
    }

    public Task(Date date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = new Date(date.getTime());
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    public String getStringDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                ConfigReader.getInstance().getDateFormat());
        return sdf.format(date);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDead() {
        this.state = TaskState.DEAD;
    }

    public void setAlive() {
        this.state = TaskState.ALIVE;
    }

    public boolean isAlive() {
        return (state == TaskState.ALIVE) ? true : false;
    }

    @Override
    public String toString() {
        return "Name: " + name + " Desc.: " + description + " Cont.: " + contacts + " Date: " + getDate().toString();
    }

    @Override
    public int compareTo(Object t) {
        if (!(t instanceof Task)) {
            log.error("The object to compare is not Task");
            throw new ObjectIsNotTaskException("The object to compare is not Task");
        }
        Long anotherTaskTime = ((Task) t).date.getTime();
        Long thisTime = this.date.getTime();
        return thisTime.compareTo(anotherTaskTime);
    }

    @Override
    public Object clone() {
        Task t = null;
        try {
            t = new Task((Date) this.date.clone());
            if (this.name != null) {
                t.name = this.name;
            }
            if (this.description != null) {
                t.description = this.description;
            }
            if (this.contacts != null) {
                t.contacts = this.contacts;
            }
        } catch (Exception e) {
            log.error("Something wrong in cloning.", e);
        }
        return t;
    }
}
