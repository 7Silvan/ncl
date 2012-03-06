package ua.group42.taskmanager.server;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.*;

/**
 *
 * @author Silvan
 */
public final class ServGUI extends JFrame implements ServerGuiIface {

    enum Option {

        ALL, ACTIVE
    }
    private static final Logger log = Logger.getLogger(ServGUI.class);
    private final ServerSideWrapperIface server;
    private Collection<UserModel> userList;
    private Option option = Option.ALL;
    private JScrollPane scrollPane;
    private JTable table;
    private DefaultTableModel tModel;
    private JButton regUser;
    private JButton delUser;
    private JButton banUser;
    private JButton unBanUser;
    private JTextField userNameField;
    private static final String[] TABLE_COLUMN_NAMES = {
        "User Name",
        "Status"
    };

    public ServGUI(ServerSideWrapperIface server) {
        this.server = server;
        initComponents();
        setVisible(true);
    }

    private void initComponents() {

        try {
            boolean nimbus = false;
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    nimbus = true;
                    break;
                }
            }

            if (!nimbus) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception ex) {
            log.error("Troubles with style.", ex);
            showError("Troubles with style.");
        }

        this.setSize(500, 550);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        this.getContentPane().setLayout(
                new FlowLayout(FlowLayout.LEADING, 10, 10));

        regUser = new JButton("New");
        delUser = new JButton("Remove");
        banUser = new JButton("Ban");
        unBanUser = new JButton("UnBan");

        userNameField = new JTextField(10);

        JPanel jPanel = new JPanel();

        tModel = new DefaultTableModel(null, TABLE_COLUMN_NAMES) {

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        scrollPane = new JScrollPane(table);

        regUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!"".equals(userNameField.getText())) {
                    server.regUser(userNameField.getText());
                } else {
                    showError("input user name to create");
                }
            }
        });

        delUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (table.getSelectedRow() != -1) {
                    server.delUser((String) table.getValueAt(table.getSelectedRow(), 0));
                } else {
                    showError("Choose user to remove.");
                }
            }
        });

        banUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (table.getSelectedRow() != -1) {
                    server.banUser((String) table.getValueAt(table.getSelectedRow(), 0));
                } else {
                    showError("Choose user to remove.");
                }
            }
        });

        unBanUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (table.getSelectedRow() != -1) {
                    server.unBanUser((String) table.getValueAt(table.getSelectedRow(), 0));
                } else {
                    showError("Choose user to remove.");
                }
            }
        });

        regUser.setSize(20, 60);
        delUser.setSize(20, 60);
        banUser.setSize(20, 60);
        unBanUser.setSize(20, 60);

        table.getTableHeader().setReorderingAllowed(false);

        jPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        jPanel.add(delUser);
        jPanel.add(banUser);
        jPanel.add(unBanUser);
        jPanel.add(regUser);
        jPanel.add(userNameField);

        this.getContentPane().add(scrollPane);
        this.getContentPane().add(jPanel);
        this.setVisible(true);
    }

    @Override
    public void update() {

        userList = (option.name().equals("ALL") ? server.getAllUsers() : server.getActiveUsers());

        tModel.setRowCount(0);

        for (UserModel user : userList) {
            tModel.addRow(new String[]{user.getName(),
                        (user.getState().name())
                    });
        }

        tModel.fireTableDataChanged();
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new ServGUI(null).setVisible(true);
            }
        });
    }
}
