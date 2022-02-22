package ver10.view.AuthorizedComponents;

import ver10.SharedClasses.AuthSettings;

import javax.swing.*;

public class MenuItem extends JMenuItem implements AuthorizedComponent {
    final @AuthSettings
    int authSettings;

    public MenuItem(String s, int authSettings) {
        super(s);
        this.authSettings = authSettings;
        setAuth(null);
    }

    @Override
    public int authSettings() {
        return authSettings;
    }
}

