package ver14.view.MenuBar;

import ver14.Client;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.SharedClasses.Login.AuthSettings;
import ver14.SharedClasses.UI.FontManager;
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

/**
 * Menu bar - represents the menu bar of the main {@link View} window.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MenuBar extends JMenuBar {
    /**
     * The constant menuFont.
     */
    private final static Font menuFont = FontManager.JMenus.headers;
    /**
     * The constant menuItemsFont.
     */
    private final static Font menuItemsFont = FontManager.JMenus.items;
    /**
     * The Client.
     */
    private final Client client;

    /**
     * Instantiates a new Menu bar.
     *
     * @param client the client
     * @param view   the view
     */
    public MenuBar(Client client, View view) {
        this.client = client;
        ArrayList<JComponent> start = new ArrayList<>();
        ArrayList<JComponent> middle = new ArrayList<>();
        ArrayList<JComponent> end = new ArrayList<>();
        start.add(new ProfileMenu(client, view));

        Menu settingsMenu = new Menu("settings", AuthSettings.NO_AUTH) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        IconManager.dynamicSettingsIcon.set(settingsMenu);
        view.addAuthorizedComponent(settingsMenu);
        end.add(settingsMenu);

        Menu aboutMenu = new Menu("About", AuthSettings.NO_AUTH) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        aboutMenu.add(new Credits(client));
        aboutMenu.add(new Rules(client));
        end.add(aboutMenu);

        JCheckBox sound = new JCheckBox("Sound Effects") {{
            setFont(menuItemsFont);
        }};
        sound.setSelected(true);
        sound.addActionListener(l -> {
            client.soundManager.setSoundEnabled(sound.isSelected());
        });
        settingsMenu.add(sound);

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
        view.addAuthorizedComponent(statisticsMenu);
        start.add(statisticsMenu);

        Menu serverStatusMenu = new Menu("server status", AuthSettings.NO_AUTH) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        view.addAuthorizedComponent(serverStatusMenu);
        IconManager.dynamicServerIcon.set(serverStatusMenu);
        start.add(serverStatusMenu);

        statisticsMenu.setFont(menuFont);
        for (PreMadeRequest preMadeRequest : PreMadeRequest.statistics) {
            Menu currentMenu = statisticsMenu;
            PreMadeRequest[] variations = preMadeRequest.getRequestVariations();

            if (variations.length > 0) {
                Menu newMenu = new Menu(preMadeRequest.createBuilder().getName(), preMadeRequest.authSettings);
                newMenu.setFont(currentMenu.getFont());
                view.addAuthorizedComponent(newMenu);
                currentMenu.add(newMenu);
                currentMenu = newMenu;
            }
            MenuItem parentItem = new RequestMenuItem(preMadeRequest, client, menuItemsFont);
            view.addAuthorizedComponent(parentItem);
            currentMenu.add(parentItem);

            Menu finalCurrentMenu = currentMenu;
            Arrays.stream(variations).forEach(req -> {
                MenuItem requestMenuItem = new RequestMenuItem(req, client, menuItemsFont);
                view.addAuthorizedComponent(requestMenuItem);
                finalCurrentMenu.add(requestMenuItem);
            });

        }

        SyncedJMenu connectedUsers = new ConnectedUsers();
        serverStatusMenu.add(connectedUsers.getJMenu());
        view.addListToRegister(connectedUsers);

        SyncedJMenu ongoingGames = new OngoingGames();
        serverStatusMenu.add(ongoingGames.getJMenu());
        view.addListToRegister(ongoingGames);

        start.forEach(this::add);
        if (!middle.isEmpty()) {
            add(Box.createHorizontalGlue());
            middle.forEach(this::add);
        }
        add(Box.createHorizontalGlue());
        end.forEach(this::add);
    }

    
}
