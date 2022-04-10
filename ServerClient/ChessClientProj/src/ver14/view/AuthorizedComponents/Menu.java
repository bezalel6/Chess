package ver14.view.AuthorizedComponents;

import ver14.SharedClasses.AuthSettings;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Utils.StrUtils;

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

    public JMenuItem add(String text, VoidCallback onClick) {
        return add(new MenuItem(text) {{
            addActionListener(l -> onClick.callback());
        }});
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
