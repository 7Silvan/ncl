package ua.group42.taskmanager.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.*;
import ua.group42.taskmanager.control.ConfigReader;

/**
 *
 * @author Group42
 * 
 */
public class Task implements Serializable, Comparable {

    private static final Logger log = Logger.getLogger(Task.class);
    private String name;
    private String description;
    private String contacts;
    private Date date;

    public Task(String name, String description, String contacts, Date date) {
        this.name = name;
        this.description = description;
        this.contacts = contacts;
        this.date = date;
    }

    public Task(String name, String description, String contacts) {
        this.name = name;
        this.description = description;
        this.contacts = contacts;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
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

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContacts() {
        return contacts;
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
    
    @Override
    public String toString() {
        return new StringBuilder("Name: ")
                .append(name)
                .append(" Desc.: ")
                .append(description)
                .append(" Cont.: ")
                .append(contacts)
                .append(" Date: ")
                .append(getStringDate())
                .toString();
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
