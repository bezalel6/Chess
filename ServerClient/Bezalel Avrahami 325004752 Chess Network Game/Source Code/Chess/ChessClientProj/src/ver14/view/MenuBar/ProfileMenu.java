package ver14.view.MenuBar;

import ver14.Client;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.SharedClasses.Login.AuthSettings;
import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.UI.FontManager;
import ver14.view.AuthorizedComponents.Menu;
import ver14.view.AuthorizedComponents.MenuItem;
import ver14.view.IconManager.IconManager;
import ver14.view.ProfilePnl;
import ver14.view.View;

import java.awt.*;

/**
 * represents the profile menu. showing the username and profile pic, and when clicked will
 * show all the profile actions available to the current client.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ProfileMenu extends Menu {

    /**
     * The constant menuFont.
     */
    private final static Font menuFont = FontManager.JMenus.headers;
    /**
     * The constant menuItemsFont.
     */
    private final static Font menuItemsFont = FontManager.JMenus.items;
    /**
     * The Profile pnl.
     */
    private final ProfilePnl profilePnl;

    /**
     * Instantiates a new Profile menu.
     *
     * @param client the client
     * @param view   the view
     */
    public ProfileMenu(Client client, View view) {
        super("", AuthSettings.ANY_LOGIN);
        setIcon(IconManager.defaultUserIcon);
        setFont(menuFont);

        profilePnl = new ProfilePnl();
        add(profilePnl);
        view.addAuthorizedComponent(profilePnl);
        view.addAuthorizedComponent(this);
        Menu userSettings = new Menu("user settings", AuthSettings.USER) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        view.addAuthorizedComponent(userSettings);
        add(userSettings);

        userSettings.add("change profile picture", () -> {
            client.request(PreMadeRequest.ChangeProfilePic, res -> {
                client.setProfilePic(res.getRequest().getBuilder().getArgVal(1));
            });
        });

        userSettings.add(new MenuItem("change password") {{
            addActionListener(l -> client.changePassword());
        }});

        userSettings.add(new MenuItem("delete all unfinished games") {{
            addActionListener(l -> client.delUnf());
        }});
    }

    @Override
    public boolean setAuth(LoginInfo loginInfo) {
        boolean ret = super.setAuth(loginInfo);
//        setText(loginInfo == null ? "Profile" : loginInfo.getUsername());
        setIcon(IconManager.loadUserIcon(loginInfo == null ? null : loginInfo.getProfilePic()));
        return ret;
    }
}
