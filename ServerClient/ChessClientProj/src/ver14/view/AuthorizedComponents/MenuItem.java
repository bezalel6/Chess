package ver14.view.AuthorizedComponents;

import ver14.SharedClasses.Login.AuthSettings;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;

/**
 * Menu item - represents an authorized menu item.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MenuItem extends JMenuItem implements AuthorizedComponent {
    /**
     * The Auth settings.
     */
    final @AuthSettings
    int authSettings;

    /**
     * Instantiates a new Menu item.
     *
     * @param s the s
     */
    public MenuItem(String s) {
        this(s, AuthSettings.NO_AUTH);
    }

    /**
     * Instantiates a new Menu item.
     *
     * @param s            the s
     * @param authSettings the auth settings
     */
    public MenuItem(String s, int authSettings) {
        super(StrUtils.format(s));
        this.authSettings = authSettings;
        setAuth(null);
    }

    @Override
    public int authRequirements() {
        return authSettings;
    }
}

