package ver12.view.AuthorizedComponents;

import ver12.SharedClasses.AuthSettings;
import ver12.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.awt.*;

public class Menu extends JMenu implements AuthorizedComponent {
    final @AuthSettings
    int authSettings;
    protected Font childrenFont;

    public Menu(String s, @AuthSettings int authSettings) {
        super(StrUtils.format(s));
        this.authSettings = authSettings;
        setAuth(null);
    }

    @Override
    public JMenuItem add(JMenuItem menuItem) {
        if (childrenFont != null)
            menuItem.setFont(childrenFont);
        return super.add(menuItem);
    }

    public void setChildrenFont(Font childrenFont) {
        this.childrenFont = childrenFont;
    }


    @Override
    public int authSettings() {
        return authSettings;
    }

}
