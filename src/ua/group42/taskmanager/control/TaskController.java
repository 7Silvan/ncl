package ua.group42.taskmanager.control;

import java.util.*;
import java.awt.event.*;
import org.apache.log4j.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import ua.group42.taskmanager.Listener;
import ua.group42.taskmanager.Notifiable;
import ua.group42.taskmanager.Updatable;
import ua.group42.taskmanager.model.*;
import ua.group42.taskmanager.control.data.*;

/**
 * Is controlling all events and supports all important processes
 * This class is a Singleton; you can retrieve the instance via the
 * getInstance() method.
 * @author Group42
 */
public class TaskController implements ControllerIface {

    /**
     * Instance of TaskController for Singleness
     */
    private static TaskController instance = null;
    /**
     * List for listeners that will listen for events, Conquer and Command ))
     */
    private LinkedList listeners = new LinkedList();
    /**
     * List of tasks for checking on
     */
    private static Collection<Task> watchList;
    /**
     * private TaskFolderModel model;
     */
    private static TaskDAO dao;
    private static final Logger log = Logger.getLogger(TaskController.class);
    public static final String CONFIG_FILE = "config.xml";
    public static final String VERSION = "V.0.2";

    /**
     * Sets task as postponed, adds time which stated in @link ConfigReader
     * @param taskForNotify 
     */
    @Override
    public void addPostponedTask(Task taskForNotify) throws InvalidTaskException {
        Task temp = taskForNotify;
        //adding postpone time
        Calendar c = Calendar.getInstance();
        c.setTime(temp.getDate());
        c.add(Calendar.MINUTE, ConfigReader.getInstance().getPostponeTime());
        //throwing task on watching
        temp.setDate(c.getTime());


        try {
            boolean add = watchList.add(temp);

            if (!add) {
                log.error("Task date is not unique.");
                throw new InvalidTaskException("Task date is not unique.");
            }
            
            dao.saveTask(temp);
            
        } catch (IOException ex) {
            // TODO: resolve
        }
        updated();
    }

    @Override
    public void addTask(Task task) throws InvalidTaskException {
        try {
            boolean add = watchList.add(task);

            if (!add) {
                log.error("Task date is not unique.");
                throw new InvalidTaskException("Task date is not unique.");
            }
            
            dao.saveTask(task);
        } catch (IOException ex) {
            // TODO: resolve
        }

        updated();
    }

    @Override
    public void addTask(String name, String description, String contacts, String date) throws InvalidTaskException {
        SimpleDateFormat sdf = new SimpleDateFormat(ConfigReader.DEFAULT_DATE_FORMAT);
        try {
            addTask(new Task(name, description, contacts, sdf.parse(date)));
            updated();
        } catch (ParseException ex) {
            log.error("Date Parse Error: " + date + " didn't match the pattern ("
                    + ConfigReader.DEFAULT_DATE_FORMAT + ").", ex);
            throw new InvalidTaskException("Parsing Data Error", ex);
        }
    }

    @Override
    public void editTask(Task was, Task become) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Deprecated
    public void removeTask(int id) {
        if (watchList instanceof List) {
            Task remove = (Task) ((List) watchList).remove(id);
            try {
                dao.saveAllTasks(watchList);
            } catch (IOException ex) {
                // TODO: resolve
            }
            log.info("Task under index " + id + " removed : " + remove.toString());
            updated();
        } else {
            log.fatal("Cannot use id's for operating with tasks");
            throw new RuntimeException("Internal Program Error");
        }
    }

    @Override
    public Task[] getTasks() {
        if (watchList == null) {
            try {
                watchList = dao.loadTasks();
            } catch (IOException ex) {
                throw new RuntimeException("Error with loading tasks.");
            }
        }
        return (Task[]) watchList.toArray(new Task[watchList.size()]);
    }

    @Override
    public void removeTask(Task task) {
        boolean remove = watchList.remove(task);
        try {
            dao.saveAllTasks(watchList);
        } catch (IOException ex) {
            throw new RuntimeException("Error with saving tasks after removing.");
        }
        if (!remove) log.error("didn't remove");
        updated();
    }

    /**
     * Calls for date format pattern in 
     * @see ConfigReader 
     * @return SimpleDateFormat initialized with given pattern
     */
    @Override
    public SimpleDateFormat getDateFormatter() {
        return new SimpleDateFormat(ConfigReader.getInstance().getDateFormat());
    }

    /**
     * When dao updated TC fires update to all
     */
    public synchronized void updated() {
        for (Iterator it = listeners.iterator(); it.hasNext();) {
            Listener listener = (Listener) it.next();
            if (listener instanceof Updatable) {
                ((Updatable) listener).update();
            }
        }
    }

    /**
     * 
     */
    public synchronized void taskNotifying(Task task) {
        for (Iterator it = listeners.iterator(); it.hasNext();) {
            Listener listener = (Listener) it.next();
            if (listener instanceof Notifiable) {
                ((Notifiable) listener).taskNotify(task);
            }
        }
    }

    //<editor-fold defaultstate="" desc="initialising TC and parts of it">
    private TaskController() {
        ConfigReader confReader = ConfigReader.getInstance();
        try {
            confReader.readConfig(CONFIG_FILE);
            log.debug("Config readed.");

            dao = DAOFactory.getDAO(confReader);
            log.debug("Dao matched and configured.");

            startDaemon();
            log.debug("Daemon Started.");
        } catch (IOException ex) {
            log.fatal("IO Error", ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "IO Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (BadConfigException ex) {
            log.fatal("Configuration Error", ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Configuration Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    /**
     * We will use Double Checked Idiom about Singlton implementation
     */
    public static TaskController getInstance() {
        TaskController localInstance = instance;
        if (localInstance == null) {
            synchronized (TaskController.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new TaskController();
                }
            }
        }

        return localInstance;
    }

    /**
     * Initializing daemon thread which will tick for checking Tasks.
     */
    private void startDaemon() {
        log.debug("entering startDaemon()");
        try {
            watchList = dao.loadTasks();
        } catch (IOException ex) {
            // TODO: resolve
        }

        Thread t = new Thread(new DaemonTicker());
        t.setDaemon(true);
        t.start();

    }
    //</editor-fold>

    @Override
    public synchronized void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public synchronized void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    /**
     * Using for checking first task in queue, which is List watchList,
     * on ticking of Daemon Ticker.
     */
    private void checkTasksAfterTick() {
        Long newTime = Calendar.getInstance().getTimeInMillis() / 1000;
        if (!watchList.isEmpty()) {
            // Getting charged task
            Task charge = watchList.iterator().next();
            Long compareTime = charge.getDate().getTime() / 1000;

            if (newTime >= compareTime) {
                log.info(String.format("Gotcha task named %s at %s", charge.getName(), Calendar.getInstance().getTime().toString()));

                // talks to Notifiables about rised task
                TaskController.getInstance().taskNotifying((Task) charge.clone());
                // removing task from list of working tasks
                removeTask(charge);
                // updating watchlist
                TaskController.getInstance().updated();
            }
        }
    }

    /**
     * This class is simple, it ticks  ... demonic :D
     * ... and it's a Singleton class which is implemented as 
     * "On Demand Holder"
     */
    private class DaemonTicker extends Thread {

        private Boolean stop = false;

        public void stopTheDaemon() {
            this.stop = true;
        }
        /**
         * Defines Interval for regular checking of taskList
         * Measuring in milliseconds, so 10000 msec = 10 sec.
         */
        //TODO: Candidate for config parametr
        private long checkIntervalMillis = 1000;

        private DaemonTicker() {
        }

        @Override
        public void run() {
            log.info(String.format("in Daemon run(): currentThread() is %s", Thread.currentThread().getName()));

            while (true) {
                try {
                    if (stop) {
                        break;
                    }
                    Thread.sleep(checkIntervalMillis);
                    TaskController.this.checkTasksAfterTick();
                } catch (InterruptedException ex) {
                    log.debug("InterruptedException", ex);
                }
                log.info("in Daemon run(): woke up again");
            }
        }
    }
}
