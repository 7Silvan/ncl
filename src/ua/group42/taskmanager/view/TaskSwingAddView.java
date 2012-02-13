package ua.group42.taskmanager.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import org.apache.log4j.*;
import javax.swing.*;
import ua.group42.taskmanager.control.InvalidTaskException;

/**
 *
 * @author Group42
 * 
 */
public class TaskSwingAddView extends TaskAbstractView {

    private static final Logger log = Logger.getLogger(TaskSwingAddView.class);
    private JButton addButton;
    private JTextField nameField;
    private JTextField desField;
    private JTextField conctField;
    private JFormattedTextField dateField;

    @Override
    public void createFrame() {
        frame = new JFrame("Add task:");
        addButton = new JButton(TaskAbstractView.Titles.ADD.toString());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setSize(400, 280);

        frame.getContentPane().setLayout(
                new FlowLayout(FlowLayout.CENTER, 10, 10));
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                TaskSwingView.getInstance().closeView(TaskSwingAddView.this);
            }
        });
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    TaskSwingView.getInstance().getControl()
                            .addTask(
                            nameField.getText(),
                            desField.getText(),
                            conctField.getText(),
                            dateField.getText());
                    TaskSwingView.getInstance().closeView(TaskSwingAddView.this);
                    log.debug("added new Task with name :" 
                            + nameField.getText() 
                            + " and will rise at : " 
                            + dateField.getText());
                } catch (InvalidTaskException ex) {
                    log.error("Invalid Task Params", ex);
                    showError(ex.getMessage());
                }
            }
        });
        nameField = new JTextField("Name", 10);
        desField = new JTextField("Description", 10);
        conctField = new JTextField("Contact", 10);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 7);
        
        dateField = new JFormattedTextField(TaskSwingView.getInstance().getControl().getDateFormatter().format(cal.getTime()));

        frame.getContentPane().add(addButton);
        frame.getContentPane().add(nameField);
        frame.getContentPane().add(desField);
        frame.getContentPane().add(conctField);
        frame.getContentPane().add(dateField);
    }

    public TaskSwingAddView() {
        super(TypeView.add);
    }
}