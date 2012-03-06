package ua.group42.taskmanager.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import org.apache.log4j.*;
import javax.swing.*;
import ua.group42.taskmanager.control.InternalControllerException;
import ua.group42.taskmanager.model.InvalidTaskException;
import ua.group42.taskmanager.model.Task;

/**
 *
 * @author Group42
 * 
 */
public class TaskSwingEditView extends TaskAbstractView {

    private static final Logger log = Logger.getLogger(TaskSwingAddView.class);
    private SimpleDateFormat sdf = new SimpleDateFormat(
            TaskSwingView.getInstance().getControl().getDateFormat());
            
    private JButton editButton;
    private JButton closeButton;
    private JTextField nameField;
    private JTextField desField;
    private JTextField idField;
    private JFormattedTextField dateField;
    
    private Task task4Edit;

    @Override
    public void createFrame() {
        frame = new JFrame("Edit task:");
        editButton = new JButton(TaskAbstractView.Titles.EDIT.toString());
        closeButton = new JButton(TaskAbstractView.Titles.CLOSE.toString());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setSize(400, 280);

        frame.getContentPane().setLayout(
                new FlowLayout(FlowLayout.CENTER, 10, 10));
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                TaskSwingView.getInstance().closeView(TaskSwingEditView.this);
            }
        });
        
        
        nameField = new JTextField(null, 10);
        desField = new JTextField(null, 10);
        idField = new JTextField(null, 10);
        idField.setEditable(false);

        dateField = new JFormattedTextField("Date");
        
        editButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
//                    TaskSwingView.getInstance().getControl()
//                            .removeTask(task4Edit);
//                    TaskSwingView.getInstance().getControl()
//                            .addTask(
//                            idField.getText(),
//                            nameField.getText(),
//                            desField.getText(),
//                            dateField.getText());
                    TaskSwingView.getInstance().getControl()
                            .editTask(
                            task4Edit.getId(), 
                            nameField.getText(),
                            desField.getText(),
                            dateField.getText());
                    TaskSwingView.getInstance().closeView(TaskSwingEditView.this);
                    log.info("edited Task with name :" 
                            + task4Edit.getName() 
                            + " and will rise at : " 
                            + dateField.getText());
                } catch (InvalidTaskException ex) {
                    log.error("Invalid Task Params", ex);
                    showError(ex.getMessage());
                } catch (InternalControllerException ex) {
                    log.error("Controller unhandled error: " + ex.getMessage());
                    showError("Controller unhandled error: " + ex.getMessage());
                }
            }
        });
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                TaskSwingView.getInstance().closeView(TaskSwingEditView.this);
            }
        });
        

        frame.getContentPane().add(editButton);
        frame.getContentPane().add(closeButton);
        frame.getContentPane().add(idField);
        frame.getContentPane().add(nameField);
        frame.getContentPane().add(desField);
        frame.getContentPane().add(dateField);
    }

    public TaskSwingEditView(Task task) {
        super(TypeView.edit);
        
        task4Edit = task;
        
        update();
    }
    
    private void update() {
        idField.setText(task4Edit.getId());
        nameField.setText(task4Edit.getName());
        desField.setText(task4Edit.getDescription());
        dateField.setText(sdf.format(task4Edit.getDate()));
    }
}