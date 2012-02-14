package ua.group42.taskmanager.view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import ua.group42.taskmanager.model.*;
import org.apache.log4j.*;
import ua.group42.taskmanager.model.InternalControllerException;
import ua.group42.taskmanager.view.TypeView;

/**
 *
 * @author Group42
 * 
 */
public class TaskSwingNotifyView extends TaskAbstractView {

    private static final Logger log = Logger.getLogger(TaskSwingNotifyView.class.getName());
    final private Task taskForNotify;
    private JTextField nameField;
    private JTextField desField;
    private JTextField contField;
    private JFormattedTextField dateField;

    @Override
    protected void createFrame() {
        frame = new JFrame("Hey! Your event has come!");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setAlwaysOnTop(true);
        JButton postponeButton = new JButton(TaskAbstractView.Titles.POSTPONE.toString());
        postponeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    TaskSwingView.getInstance().getControl()
                            .addPostponedTask(taskForNotify);
                    TaskSwingView.getInstance().closeView(TaskSwingNotifyView.this);
                } catch (InvalidTaskException ex) {
                    log.error("Invalid Task Params", ex);
                    showError("Invalid Task Params" + ex.getMessage());
                } catch (InternalControllerException ex) {
                    showError("Controller unhandled error: " + ex.getMessage());
                }
            }
        });

        JButton closeButton = new JButton(TaskAbstractView.Titles.CLOSE.toString());
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                TaskSwingView.getInstance().closeView(TaskSwingNotifyView.this);
            }
        });

        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        nameField = new JTextField(null, 10);
        desField = new JTextField(null, 10);
        contField = new JTextField(null, 10);
        dateField = new JFormattedTextField(new Date());

        jPanel1.add(postponeButton);
        jPanel1.add(closeButton);
        jPanel1.add(nameField);
        jPanel1.add(desField);
        jPanel1.add(contField);
        jPanel1.add(dateField);
        frame.getContentPane().add(jPanel1);
    }

    public TaskSwingNotifyView(Task task) {
        
        super(TypeView.notify);

        taskForNotify = task;

        update();
    }

    private void update() {
        nameField.setText(taskForNotify.getName());
        desField.setText(taskForNotify.getDescription());
        contField.setText(taskForNotify.getContacts());
        dateField.setText(taskForNotify.getStringDate());
    }

}
