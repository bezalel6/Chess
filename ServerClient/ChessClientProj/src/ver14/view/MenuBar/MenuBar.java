package ver14.view.MenuBar;

import ver14.Client;
import ver14.SharedClasses.AuthSettings;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.SharedClasses.FontManager;
import ver14.view.AuthorizedComponents.AuthorizedComponent;
import ver14.view.AuthorizedComponents.Menu;
import ver14.view.AuthorizedComponents.MenuItem;
import ver14.view.IconManager.IconManager;
import ver14.view.MenuBar.MessageItems.Credits;
import ver14.view.MenuBar.MessageItems.Rules;
import ver14.view.SyncedJMenus.ConnectedUsers;
import ver14.view.SyncedJMenus.OngoingGames;
import ver14.view.SyncedJMenus.SyncedJMenu;
import ver14.view.View;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MenuBar extends JMenuBar {
    private final static Font menuFont = FontManager.JMenus.headers;
    private final static Font menuItemsFont = FontManager.JMenus.items;
    private final Client client;

    public MenuBar(ArrayList<AuthorizedComponent> authorizedComponents, Client client, View view) {
        this.client = client;
        ArrayList<JComponent> start = new ArrayList<>();
        ArrayList<JComponent> middle = new ArrayList<>();
        ArrayList<JComponent> end = new ArrayList<>();

        Menu settingsMenu = new Menu("settings", AuthSettings.NO_AUTH) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        IconManager.dynamicSettingsIcon.set(settingsMenu);
        authorizedComponents.add(settingsMenu);
        end.add(settingsMenu);

        Menu aboutMenu = new Menu("About", AuthSettings.NO_AUTH) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        aboutMenu.add(new Credits(client));
        aboutMenu.add(new Rules(client));
        end.add(aboutMenu);

        MenuItem flipBoard = new MenuItem("Flip Board");
        flipBoard.addActionListener(l -> {
            view.flipBoard();
        });
        settingsMenu.add(flipBoard);

        Menu statisticsMenu = new Menu("statistics", AuthSettings.ANY_LOGIN) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        IconManager.dynamicStatisticsIcon.set(statisticsMenu);
        authorizedComponents.add(statisticsMenu);
        start.add(statisticsMenu);

        Menu serverStatusMenu = new Menu("server status", AuthSettings.NO_AUTH) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        authorizedComponents.add(serverStatusMenu);
        IconManager.dynamicServerIcon.set(serverStatusMenu);
        start.add(serverStatusMenu);

        Menu userSettings = new Menu("user settings", AuthSettings.USER) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        authorizedComponents.add(userSettings);
        settingsMenu.add(userSettings);

        userSettings.add(new MenuItem("change password") {{
            addActionListener(l -> MenuBar.this.client.changePassword());
        }});

        userSettings.add(new MenuItem("delete all unfinished games") {{
            addActionListener(l -> MenuBar.this.client.delUnf());
        }});

        statisticsMenu.setFont(menuFont);
        for (PreMadeRequest preMadeRequest : PreMadeRequest.statistics) {
            Menu currentMenu = statisticsMenu;
            PreMadeRequest[] variations = preMadeRequest.getRequestVariations();

            if (variations.length > 0) {
                Menu newMenu = new Menu(preMadeRequest.createBuilder().getName(), preMadeRequest.authSettings);
                newMenu.setFont(currentMenu.getFont());
                authorizedComponents.add(newMenu);
                currentMenu.add(newMenu);
                currentMenu = newMenu;
            }
            MenuItem parentItem = createRequestMenuItem(preMadeRequest, menuItemsFont);
            authorizedComponents.add(parentItem);
            currentMenu.add(parentItem);

            Menu finalCurrentMenu = currentMenu;
            Arrays.stream(variations).forEach(req -> {
                MenuItem requestMenuItem = createRequestMenuItem(req, menuItemsFont);
                authorizedComponents.add(requestMenuItem);
                finalCurrentMenu.add(requestMenuItem);
            });

        }

//        // תת תפריטים עבור התפריט הראשי אודות
//        // ----------------------------------------
//        JMenuItem rulesMenuItem = new JMenuItem("Game & Rules");
//        rulesMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String title = "About Game & Rules";
//
//                String msg = "Tic-Tac-Toe is a game for two players,\n";
//                msg += "X and O, who take turns marking the\n";
//                msg += "spaces in a 3×3 grid.\n\n";
//                msg += "The player who succeeds in placing\n";
//                msg += "three of their marks in a horizontal,\n";
//                msg += "vertical, or diagonal row wins the game.\n\n";
//
//                JOptionPane.showMessageDialog(win, msg, title, JOptionPane.PLAIN_MESSAGE);
//            }
//        });
//
//        // תת תפריט לקרדיטים
//        JMenuItem creditsMenuItem = new JMenuItem("Credits");
//        creditsMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String title = "Credits";
//
//                String msg = "Programming:\n";
//                msg += "1. \n";
//                msg += "2. Kiryat Noaar Students.\n\n";
//
//                msg += "Sound & Music & Graphics:\n";
//                msg += "1. freesound.org\n";
//                msg += "2. UI Designer - Ilan Perets.\n";
//                msg += "3. Icons & Images - www.flaticon.com\n\n";
//
//                msg += "All rights reserved (c) 2021\n";
//
//                JOptionPane.showMessageDialog(win, msg, title, JOptionPane.PLAIN_MESSAGE);
//            }
//        });

        SyncedJMenu connectedUsers = new ConnectedUsers();
        serverStatusMenu.add(connectedUsers.getJMenu());
        view.addListToRegister(connectedUsers);
//        listsToRegister.add(connectedUsers);

        SyncedJMenu ongoingGames = new OngoingGames();
        serverStatusMenu.add(ongoingGames.getJMenu());
        view.addListToRegister(ongoingGames);
//        listsToRegister.add(ongoingGames);

        start.forEach(this::add);
        if (!middle.isEmpty()) {
            add(Box.createHorizontalGlue());
            middle.forEach(this::add);
        }
        add(Box.createHorizontalGlue());
        end.forEach(this::add);
    }


    private MenuItem createRequestMenuItem(PreMadeRequest preMadeRequest, Font menuItemsFont) {
        return new MenuItem(preMadeRequest.createBuilder().getName(), preMadeRequest.authSettings) {{
            addActionListener(l -> {
                client.request(preMadeRequest);
            });
            setFont(menuItemsFont);
        }};

    }
}
