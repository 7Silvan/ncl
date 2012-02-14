package ua.group42.taskmanager.control;

import java.util.*;
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
     * Instance of Ticker of TaskController (@see DaemonTicker)
     */
    private Ticker tickerInstance;
    /**
     * List for listeners that will listen for events, Conquer and Command ))
     */
    private List<Listener> listeners = new LinkedList<Listener>();
    /**
     * List of tasks for checking on
     */
    private Collection<Task> watchList;
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

            boolean add;
            synchronized (watchList) {
                add = watchList.add(temp);
            }
            if (!add) {
                log.error("Task date is not unique.");
                throw new InvalidTaskException("Task date is not unique.");
            }
            
            dao.saveTask(temp);
        updated();
    }

    @Override
    public void addTask(Task task) {
            boolean add;
            synchronized (watchList) {
                add = watchList.add(task);
            }

            if (!add) {
                log.error("Task date is not unique.");
                throw new InvalidTaskException("Task date is not unique.");
            }
            
            dao.saveTask(task);
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
    public void editTask(Task was, Task become) throws InvalidTaskException {
        removeTask(was);
        addTask(become);
    }

    @Override
    public Collection<Task> getTasks() {
        if (watchList == null) {
                watchList = dao.loadTasks();
        }
        return Collections.unmodifiableCollection(watchList);
    }

    @Override
    public void removeTask(Task task) {
        boolean remove;
        synchronized(watchList) {
            remove = watchList.remove(task);
        }
            dao.saveAllTasks(watchList);
            
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

        watchList = dao.loadTasks();

        tickerInstance = new Ticker();
        tickerInstance.start();

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

    @Override
    public void stopService() {
        log.info("Asked for stopping service. Stopping ...");
        tickerInstance.stopTheThread();
    }

    /**
     * This class is simple, it ticks  ... demonic :D (even it's not Daemon anymore :)
     * ... and it's a Singleton class which is implemented as 
     * "On Demand Holder"
     */
    private class Ticker extends Thread {

        private Boolean stop = false;

        public void stopTheThread() {
            this.stop = true;
        }
        /**
         * Defines Interval for regular checking of taskList
         * Measuring in milliseconds, so 10000 msec = 10 sec.
         */
        //TODO: Candidate for config parametr
        private long checkIntervalMillis = 1000;

        private Ticker() {
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
