package ver14.SharedClasses.UI.dialogs;

import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.UI.MyLbl;

import javax.swing.*;
import java.awt.*;


/**
 * The utility class Confirm dialogs.
 */
public class ConfirmDialogs {
    /**
     * Confirm boolean.
     *
     * @param parent  the parent
     * @param title   the title
     * @param message the message
     * @param icon    the icon
     * @return the boolean
     */
    public static boolean confirm(Component parent, String title, String message, ImageIcon icon) {
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon) == JOptionPane.YES_OPTION;
    }

    /**
     * Confirm string.
     *
     * @param parent       the parent
     * @param header       the header
     * @param message      the message
     * @param title        the title
     * @param icon         the icon
     * @param initialValue the initial value
     * @return the string
     */
    public static String confirm(Component parent, String title, String header, String message, ImageIcon icon, String initialValue) {

        return (String) JOptionPane.showInputDialog(parent, new JPanel(new GridLayout(0, 1)) {{
            add(new MyLbl(header, FontManager.Dialogs.dialog));
            add(new MyLbl(message, FontManager.Dialogs.dialog));
        }}, title, JOptionPane.QUESTION_MESSAGE, icon, null, initialValue);
    }
}
