package ua.group42.taskmanager.view;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.*;
import ua.group42.taskmanager.control.ConfigReader;
import ua.group42.taskmanager.control.ControllerIface;
import ua.group42.taskmanager.model.InternalControllerException;
import ua.group42.taskmanager.model.*;

/**
 *
 * @author Group42
 * 
 */
public class TaskSwingView extends TaskAbstractView implements MainView {

    static ControllerIface controller = null;
    public static final Logger log = Logger.getLogger(TaskSwingView.class);
    private static volatile TaskSwingView instance;
    private DefaultTableModel tModel;
    private JTable table;
    public static final String[] TABLE_COLUMN_NAMES = {
        "Name",
        "Description",
        "Contacts",
        "Date"
    };
    private Collection<TaskView> views = new LinkedList<TaskView>();

    /**
     * Registering control for acquiring controlled actions
     * @param control interface object
     * @return true if control assigned (it was null), otherwise false
     */
    @Override
    public boolean regController(ControllerIface control) {
        if (controller == null) {
            controller = control;
            return true;
        } else {
            return false;
        }
    }

    private TaskSwingView() {
        super(TypeView.main);
    }

    public static synchronized TaskSwingView getInstance() {
        if (instance == null) {
            instance = new TaskSwingView();
            instance.subscribeView(instance);
        }
        return instance;
    }

    @Override
    public void createFrame() {
        frame = new JFrame(Titles.FRAME.toString());

        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setSize(800, 480);
        frame.getContentPane().setLayout(
                new FlowLayout(FlowLayout.LEADING, 10, 10));

        //declaring control buttons
        JButton removeButton = new JButton(Titles.REMOVE.toString());
        JButton addButton = new JButton(Titles.ADD.toString());
        JButton editButton = new JButton(Titles.EDIT.toString());

        JPanel jPanel = new JPanel();

        tModel = new DefaultTableModel(null, TABLE_COLUMN_NAMES) {

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };


        table = new JTable(tModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // wrapping table with scrollPane
        JScrollPane scrollPane = new JScrollPane(table);

        String columnName = table.getModel().getColumnName(0);
        JLabel headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setText(columnName);

        TableColumnModel columnModel = table.getColumnModel();
        TableColumn cc = columnModel.getColumn(0);
        cc.setHeaderRenderer((TableCellRenderer) headerRenderer);

        tModel.addTableModelListener(table);

        // declaring acitonListeners on buttons
        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (getSelectedTaskID() > -1) {
                        getControl().removeTask(getSelectedTask());
                    } else {
                        showError("Removing: no task selected");
                        log.info("Removing: no task selected");
                    }
                } catch (InternalControllerException ex) {
                    showError("Controller Error: " + ex.getMessage());
                }

            }
        });
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createView(TypeView.add).show();
            }
        });
        editButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (getSelectedTaskID() > -1) {
                        Task task = getSelectedTask();
                        createView(TypeView.edit, task).show();
                    } else {
                        showError("Editing: no task selected");
                        log.info("Editing: no task selected");
                    }
                } catch (InternalControllerException ex) {
                    showError("Controller Error: " + ex.getMessage());
                }
            }
        });

        removeButton.setSize(20, 60);
        addButton.setSize(20, 60);
        editButton.setSize(20, 60);

        table.getTableHeader().setReorderingAllowed(false);

        jPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        jPanel.add(addButton);
        jPanel.add(editButton);
        jPanel.add(removeButton);

        frame.getContentPane().add(scrollPane);
        frame.getContentPane().add(jPanel);
        frame.setVisible(true);
    }

    private TaskView createView(TypeView type, Task task) {
        TaskView view = TaskViewFactory.createView(type, task);
        subscribeView(view);
        return view;
    }

    private TaskView createView(TypeView type) {
        return createView(type, null);
    }

    /**
     * Updates the view with data taked from dao
     */
    @Override
    public synchronized void update() {
        tModel.setRowCount(0);
        String stringDateFormat = ConfigReader.getInstance().getDateFormat();
        SimpleDateFormat format = new SimpleDateFormat(stringDateFormat, Locale.US);

        Collection<Task> taskList = getControl().getTasks();

        for (Task tTask : taskList) {
            tModel.addRow(new String[]{tTask.getName(),
                        tTask.getDescription(),
                        tTask.getContacts(),
                        format.format(tTask.getDate())
                    });
        }

        tModel.fireTableDataChanged();

    }

    /**
     * Says what id has the task selected at view now
     * @return position of task in table starting from 0 as first, and -1 if there no position selected
     */
    private int getSelectedTaskID() {
        synchronized (table) {
            return table.getSelectedRow();
        }
    }

    private Task getSelectedTask() {
        synchronized (table) {
            int temp = table.getSelectedRow();
            List<String> list = new LinkedList<String>();
            for (int i = 0; i < table.getColumnCount() - 1; i++) {
                list.add((String) table.getValueAt(temp, i));
            }
            String dateStr = (String) table.getValueAt(temp, table.getColumnCount() - 1);
            Date date;
            try {
                date = getControl().getDateFormatter().parse(dateStr);

                return new Task(list.get(0), list.get(1), list.get(2), date);
            } catch (ParseException ex) {
                log.error("Parsing date error", ex);
                showError("Parsing date error");
            }

            return null;
        }
    }

    /**
     * Subscribing created view for performing actions
     * @param view new view
     */
    private void subscribeView(TaskView view) {
        views.add(view);
    }

    /**
     * UnSubscribing created view for performing actions
     * @param view new view
     */
    private void unSubscribeView(TaskView view) {
        views.remove(view);
    }

    @Override
    public void closeView(TaskView view) {
        unSubscribeView(view);
        view.close();
    }

    @Override
    public void showAll() {
        for (Iterator it = views.iterator(); it.hasNext();) {
            ((TaskView) it.next()).show();
        }
    }

    @Override
    public void hideAll() {
        for (Iterator it = views.iterator(); it.hasNext();) {
            ((TaskView) it.next()).hide();
        }
    }

    @Override
    public void taskNotify(Task task) {
        createView(TypeView.notify, task).show();
    }

    @Override
    public ControllerIface getControl() {
        if (controller == null) {
            throw new IllegalAccessError("No Controller registered.");
        }
        return controller;
    }
}
