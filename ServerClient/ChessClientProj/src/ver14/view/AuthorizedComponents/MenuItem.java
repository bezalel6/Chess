package ver14.view.AuthorizedComponents;

import ver14.SharedClasses.AuthSettings;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;

public class MenuItem extends JMenuItem implements AuthorizedComponent {
    final @AuthSettings
    int authSettings;

    public MenuItem(String s) {
        this(s, AuthSettings.NO_AUTH);
    }

    public MenuItem(String s, int authSettings) {
        super(StrUtils.format(s));
        this.authSettings = authSettings;
        setAuth(null);
    }

    @Override
    public int authSettings() {
        return authSettings;
    }
}
