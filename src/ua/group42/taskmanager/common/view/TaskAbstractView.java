package ua.group42.taskmanager.common.view;

import javax.swing.*;
import org.apache.log4j.*;

/**
 *
 * TaskManager 
 * 
 * @author Group42
 * 
 */
public abstract class TaskAbstractView implements TaskView {

    /**
     * Every view has frame
     */
    protected JFrame frame;
    /**
     * Every view has type
     */
    protected TypeView type;
    private static final Logger log = Logger.getLogger(TaskAbstractView.class);

    public TaskAbstractView(TypeView typeOfNewView) {
        type = typeOfNewView;

        try {
            boolean nimbus = false;
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    nimbus = true;
                    break;
                }
            }
            
            if (!nimbus)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }
        createFrame();
    }

    protected abstract void createFrame();

    /**
     * Disposing the view window
     */
    @Override
    public void close() {
        frame.setVisible(false);
        frame.dispose();
    }

    @Override
    public void hide() {
        frame.setVisible(false);
        log.debug(type.name() + "-view hided");
    }

    @Override
    public void show() {
        frame.setVisible(true);
        log.debug(type.name() + "-view showed");
    }

    @Override
    public TypeView getType() {
        return type;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Titles for components on the views
     */
    public enum Titles {

        FRAME {

            @Override
            public String toString() {
                return "Task Manager";
            }
        },
        REMOVE {

            @Override
            public String toString() {
                return "Remove";
            }
        },
        ADD {

            @Override
            public String toString() {
                return "Add";
            }
        },
        EDIT {

            @Override
            public String toString() {
                return "Edit";
            }
        },
        CLOSE {

            @Override
            public String toString() {
                return "Close";
            }
        },
        POSTPONE {

            @Override
            public String toString() {
                return "Postpone";
            }
        }
    }
}
