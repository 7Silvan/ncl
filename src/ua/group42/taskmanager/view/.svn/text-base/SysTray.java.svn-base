package ua.group42.taskmanager.view;

import javax.swing.JOptionPane;
import snoozesoft.systray4j.*;
import ua.group42.taskmanager.control.TaskController;

/**
 *
 * @author Group42
 * 
 */
public class SysTray implements SysTrayMenuListener {

    // supposing that this listener is main
    private final MainView listener;

    public static enum Command {

        showMain, show, hide, about, exit
    };
    static final SysTrayMenuIcon icon = new SysTrayMenuIcon("icons/duke");
    SysTrayMenu menu;

    public SysTray(MainView listener) {
        this.listener = listener;
        createMenu();
    }

    @Override
    public void menuItemSelected(SysTrayMenuEvent stme) {
        Command com = Command.valueOf(stme.getActionCommand());
        switch (com) {
            case exit:
                System.exit(0);
            case about:
                JOptionPane.showMessageDialog(null, "TaskManager made by group42 as lab for NetCracker Course. \n"
                        + "Authors :  \n"
                        + "Lazarenko Taras \n"
                        + "Gural Roman \n"
                        + " \n"
                        + "Version : " + TaskController.VERSION);
                break;
            case showMain:
                ((TaskView) listener).show();
                break;
            case show:
                //TaskController.getInstance().showAllViews();
                listener.showAll();
                break;
            case hide:
                //TaskController.getInstance().hideAllViews();
                listener.hideAll();
                break;
            default:
                JOptionPane.showMessageDialog(null, stme.getActionCommand());
        }
    }

    @Override
    public void iconLeftClicked(SysTrayMenuEvent stme) {
        menuItemSelected(new SysTrayMenuEvent(null, Command.showMain.name()));
    }

    @Override
    public void iconLeftDoubleClicked(SysTrayMenuEvent stme) {
        menuItemSelected(new SysTrayMenuEvent(null, Command.showMain.name()));
    }

    private void createMenu() {
        SysTrayMenuItem hideItem = new SysTrayMenuItem("Hide Windows", Command.hide.name());
        hideItem.addSysTrayMenuListener(this);

        SysTrayMenuItem showItem = new SysTrayMenuItem("Show Windows", Command.show.name());
        showItem.addSysTrayMenuListener(this);

        SysTrayMenuItem itemExit = new SysTrayMenuItem("Exit", Command.exit.name());
        itemExit.addSysTrayMenuListener(this);

        SysTrayMenuItem itemAbout = new SysTrayMenuItem("About...", Command.about.name());
        itemAbout.addSysTrayMenuListener(this);

        SysTrayMenuItem itemShowMain = new SysTrayMenuItem("Show Main Window", Command.showMain.name());
        itemShowMain.addSysTrayMenuListener(this);

        // constructin the menu
        menu = new SysTrayMenu(icon);

        // constructing he menu's items
        menu.addItem(itemExit);
        menu.addSeparator();
        menu.addItem(itemAbout);
        menu.addSeparator();
        menu.addItem(itemShowMain);
        menu.addItem(showItem);
        menu.addItem(hideItem);
    }
}
