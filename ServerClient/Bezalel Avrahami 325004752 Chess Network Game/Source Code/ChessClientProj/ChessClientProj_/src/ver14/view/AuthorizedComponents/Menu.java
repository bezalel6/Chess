package ver14.view.AuthorizedComponents;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Login.AuthSettings;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Menu - represents an authorized jmenu.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Menu extends JMenu implements AuthorizedComponent {
    /**
     * The Auth settings.
     */
    final @AuthSettings
    int authSettings;
    /**
     * The Children font.
     */
    protected Font childrenFont;

    /**
     * Instantiates a new Menu with {@code authSettings} as the requirements
     *
     * @param s            the s
     * @param authSettings the auth settings
     */
    public Menu(String s, @AuthSettings int authSettings) {
        super(StrUtils.format(s));
        this.authSettings = authSettings;
        setAuth(null);
    }

    /**
     * Add j menu item.
     *
     * @param text    the text
     * @param onClick the on click
     * @return the j menu item
     */
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

    /**
     * Sets children font.
     *
     * @param childrenFont the children font
     */
    public void setChildrenFont(Font childrenFont) {
        this.childrenFont = childrenFont;
    }


    @Override
    public int authRequirements() {
        return authSettings;
    }

}
