package ua.group42.taskmanager.control;

import java.util.*;
import org.apache.log4j.*;
import java.text.*;
import javax.swing.JOptionPane;
import ua.group42.taskmanager.*;
import ua.group42.taskmanager.model.*;
import ua.group42.taskmanager.control.data.*;
import ua.group42.taskmanager.configuration.*;
import ua.group42.taskmanager.tools.Tools;

/**
 * Is controlling all events and supports all important processes
 * This class is a Singleton; you can retrieve the instance via the
 * getInstance() method.
 * @author Group42
 */
public class TaskController implements ControllerIface {

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
    private ConfigReader configuration;
    private static final Logger log = Logger.getLogger(TaskController.class);
    public static final String VERSION = "V.1.1";

    /**
     * Search for duplicate id in exsting tasks
     * @param id which is testing
     * @return result of matching
     */
    private boolean isIdFree(String id) {
        if (id == null) {
            return false;
        }
        for (Task task : watchList) {
            if (task.getId().equals(id)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Method for complex generating and validating id
     */
    private String nextTaskID() {
        String result = null;
        Integer length = null;
        String lengthParam = configuration.getParam("idLength");

        if (lengthParam == null) {
            length = new Integer(7);
        } else {
            length = Integer.parseInt(lengthParam);
        }

        do {
            result = Tools.nextID(length);
        } while (!this.isIdFree(result));
        return result;
    }

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
        c.add(Calendar.MINUTE, configuration.getPostponeTime());
        //throwing task on watching
        temp.setDate(c.getTime());

        boolean add;
        synchronized (watchList) {
            add = watchList.add(temp);
        }
        if (!add) {
            log.error("Task is not unique.");
            throw new InvalidTaskException("Task is not unique.");
        }

        dao.saveTask(temp);
        updated();
    }

    @Override
    public void addTask(Task task) {
        boolean add;
        synchronized (watchList) {
            if (!this.isIdFree(task.getId())) {
                task.setId(nextTaskID());
            }
            add = watchList.add(task);
        }

        if (!add) {
            log.error("Task date is not unique.");
            throw new InvalidTaskException("Task date is not unique.");
        }

        synchronized (watchList) {
            dao.saveTask(task);
        }
        updated();
    }

    @Override
    public void addTask(String id, String name, String description, String date) throws InvalidTaskException {
        SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat());
        try {
            if (!this.isIdFree(id)) {
                id = nextTaskID();
            }
            addTask(new Task(id, name, description, sdf.parse(date)));
        } catch (ParseException ex) {
            log.error("Date Parse Error: " + date + " didn't match the pattern ("
                    + getDateFormat() + ").", ex);
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
        synchronized (watchList) {
            remove = watchList.remove(task);
        }

        if (!remove) {
            log.error("Task was not removed.");
            throw new InternalControllerException("Task was not removed.");
        }

        synchronized (watchList) {
            dao.saveAllTasks(watchList);
        }
        updated();
    }

    @Override
    public void removeTask(String id) {
        synchronized (watchList) {
            Iterator<Task> it = Collections.synchronizedCollection(watchList).iterator();
            Task task = null;
            while (it.hasNext()) {
                task = it.next();
                if (task.getId().equals(id)) {
                    it.remove();
                }
            }
            dao.saveAllTasks(watchList);
        }
        updated();
    }

    /**
     * Calls for date format pattern in 
     * @see ConfigReader 
     * @return SimpleDateFormat initialized with given pattern
     */
    @Override
    public String getDateFormat() {
        return configuration.getDateFormat();
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
        updated();
    }
    
    public TaskController(ConfigReader confReader) {
        try {
            this.configuration = confReader;

            dao = DAOFactory.getDAO(confReader);
            log.debug("Dao matched and configured.");

            startDaemon(confReader);
            log.debug("Daemon Started.");
        } catch (BadConfigException ex) {
            log.fatal("Configuration Error", ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Configuration Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

//    /**
//     * We will use Double Checked Idiom about Singlton implementation
//     */
//    public static TaskController getInstance() {
//        TaskController localInstance = instance;
//        if (localInstance == null) {
//            synchronized (TaskController.class) {
//                localInstance = instance;
//                if (localInstance == null) {
//                    instance = localInstance = new TaskController();
//                }
//            }
//        }
//        return localInstance;
//    }
    /**
     * Initializing daemon thread which will tick for checking Tasks.
     */
    private void startDaemon(ConfigReader configReader) {
        log.debug("entering startDaemon()");

        watchList = dao.loadTasks();

        tickerInstance = new Ticker(configReader);
        tickerInstance.start();
    }

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

                // talks to Notifiables about raised task
                TaskController.this.taskNotifying((Task) charge.clone());
                // removing task from list of working tasks
                removeTask(charge);
                // updating watchlist
                //TaskController.this.updated();
            }
        }
    }

    @Override
    public void stopService() {
        log.info("Asked for stopping service. Stopping ...");
        tickerInstance.stopTheThread();
    }

    @Override
    public void addTask(String name, String description, String date) throws InvalidTaskException, InternalControllerException {
        addTask(null, name, description, date);
    }

    @Override
    public void editTask(String idTaskWas, Task become) throws InvalidTaskException, InternalControllerException {
        removeTask(idTaskWas);
        addTask(become);
    }

    @Override
    public void editTask(String idWas, String newName, String newDescription, String newDate) throws InvalidTaskException, InternalControllerException {
        removeTask(idWas);
        addTask(newName, newDescription, newDate);
    }

    @Override
    public ConfigReader getConfigReader() {
        return configuration;
    }

    /**
     * This class is simple, it ticks  ... demonic :D (even it's not Daemon anymore :)
     * ... and it's a Singleton class which is implemented as 
     * "On Demand Holder"
     */
    private class Ticker extends Thread {

        private final long DEFAULT_CHECK_INTERVAL_MILLIS = 1000;
        private Boolean stop = false;

        public void stopTheThread() {
            this.stop = true;
        }
        /**
         * Defines Interval for regular checking of taskList
         * Measuring in milliseconds, so 10000 msec = 10 sec.
         */
        private Long checkIntervalMillis;

        private Ticker(ConfigReader configReader) {
            checkIntervalMillis = (Long) ((configReader.getParam("tickTime") == null) ? DEFAULT_CHECK_INTERVAL_MILLIS : Long.parseLong(configReader.getParam("tickTime")));
        }

        @Override
        public void run() {
            log.info(String.format("in Ticker run(): currentThread() is %s", Thread.currentThread().getName()));

            while (true) {
                if (stop) {
                    break;
                }
                try {
                    Thread.sleep(checkIntervalMillis);
                    TaskController.this.checkTasksAfterTick();
                } catch (InterruptedException ex) {
                    log.debug("InterruptedException", ex);
                }
                //log.debug("in Ticker run(): woke up again");
            }
            
            log.debug("Ticker finished.");
        }
    }
}