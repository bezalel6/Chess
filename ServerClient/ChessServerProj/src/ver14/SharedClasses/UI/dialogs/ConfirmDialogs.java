package ver14.SharedClasses.UI.dialogs;

import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.awt.*;

/*
 * ConfirmDialogs
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * ConfirmDialogs -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * ConfirmDialogs -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class ConfirmDialogs extends MyDialogs {
    public static boolean confirm(Component parent, String title, String message, ImageIcon icon) {
        message = StrUtils.wrapInHtml(message);
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon) == JOptionPane.YES_OPTION;
    }
}
