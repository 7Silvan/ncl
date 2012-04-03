package ua.group42.taskmanager.server.view;

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
import ua.group42.taskmanager.server.ServerWrapperIface;
import ua.group42.taskmanager.server.model.UserModel;

/**
 *
 * @author Silvan
 */
public final class ServerGui extends JFrame implements ServerGuiIface {
    
    enum Option {
        
        ALL, ACTIVE
    }
    private static final Logger log = Logger.getLogger(ServerGui.class);
    private final ServerWrapperIface server;
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
    
    private JButton exitButton;
    
//    private JButton secretButton;
            
    private static final String[] TABLE_COLUMN_NAMES = {
        "User Name",
        "Status"
    };
    
    public ServerGui(ServerWrapperIface server) {
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
        exitButton = new JButton("Exit");
//        secretButton = new JButton("Secret");
        
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
        
        exitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                log.info("Asked for stopping server.");
                server.stopServer();
            }
        });
        
//        secretButton.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                server.secret();
//            }
//        });
        
        regUser.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (!"".equals(userNameField.getText())) {
                        server.regUser(userNameField.getText());
                    } else {
                        showError("Input user name to create.");
                    }
                } catch (IllegalAccessException ex) {
                    showError(ex.getMessage());
                }
            }
        });
        
        delUser.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (table.getSelectedRow() != -1) {
                        server.delUser((String) table.getValueAt(table.getSelectedRow(), 0));
                    } else {
                        showError("Choose user to remove.");
                    }
                } catch (IllegalAccessException ex) {
                    showError(ex.getMessage());
                }
            }
        });
        
        banUser.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (table.getSelectedRow() != -1) {
                        server.banUser((String) table.getValueAt(table.getSelectedRow(), 0));
                    } else {
                        showError("Choose user to remove.");
                    }
                } catch (IllegalAccessException ex) {
                    showError(ex.getMessage());
                }
            }
        });
        
        unBanUser.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (table.getSelectedRow() != -1) {
                        server.unBanUser((String) table.getValueAt(table.getSelectedRow(), 0));
                    } else {
                        showError("Choose user to remove.");
                    }
                } catch (IllegalAccessException ex) {
                    showError(ex.getMessage());
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
        
        jPanel.add(exitButton);
        //jPanel.add(secretButton);
        
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
}
