package ver14.view.MenuBar;

import ver14.Client;
import ver14.SharedClasses.AuthSettings;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.LoginInfo;
import ver14.view.AuthorizedComponents.AuthorizedComponent;
import ver14.view.AuthorizedComponents.Menu;
import ver14.view.AuthorizedComponents.MenuItem;
import ver14.view.IconManager.IconManager;
import ver14.view.ProfilePnl;

import java.awt.*;
import java.util.ArrayList;

public class ProfileMenu extends Menu {

    private final static Font menuFont = FontManager.JMenus.headers;
    private final static Font menuItemsFont = FontManager.JMenus.items;
    private final ProfilePnl profilePnl;

    public ProfileMenu(ArrayList<AuthorizedComponent> authorizedComponents, Client client) {
        super("", AuthSettings.ANY_LOGIN);
        setIcon(IconManager.defaultUserIcon);
        setFont(menuFont);

        profilePnl = new ProfilePnl();
        add(profilePnl);
        authorizedComponents.add(profilePnl);
        authorizedComponents.add(this);
        Menu userSettings = new Menu("user settings", AuthSettings.USER) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        authorizedComponents.add(userSettings);
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
