package ver5.SharedClasses.ui.dialogs;

import ver5.SharedClasses.StrUtils;

import javax.swing.*;
import java.awt.*;

public class ConfirmDialogs extends MyDialogs {
    public static boolean confirm(Component parent, String title, String message, ImageIcon icon) {
        message = StrUtils.wrapInHtml(message);
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon) == JOptionPane.YES_OPTION;
    }
}