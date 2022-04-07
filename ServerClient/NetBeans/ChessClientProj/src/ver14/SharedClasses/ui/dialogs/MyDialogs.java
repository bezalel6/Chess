package ver14.SharedClasses.ui.dialogs;

import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.ui.MyLbl;

import javax.swing.*;
import java.awt.*;

public class MyDialogs {
    public static void main(String[] args) {
        showInfoMessage(null, "hello", "ti", null);
    }

    public static void showInfoMessage(Component parent, String message, String title, ImageIcon icon) {
        message = StrUtils.wrapInHtml(message);
        title = StrUtils.format(title);
        JOptionPane optionPane = new JOptionPane(message);
        JDialog dialog = optionPane.createDialog(title);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        dialog.dispose();
//        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE, icon);

    }

    public static void showInfo(Component parent, Component comp, String title, ImageIcon icon) {
        title = StrUtils.format(title);

//        JScrollPane scrollPane = new JScrollPane(comp);
        // make the dialog resizable
        comp.addHierarchyListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(comp);
            if (window instanceof Dialog dialog) {
                if (!dialog.isResizable()) {
                    dialog.setResizable(true);
                }
            }
        });

        JOptionPane.showMessageDialog(parent, comp, title, JOptionPane.INFORMATION_MESSAGE, icon);
    }

    public static void showErrorMessage(Component parent, String error, String title) {
        showErrorMessage(parent, error, title, null);
    }

    public static void showErrorMessage(Component parent, String error, String title, ImageIcon icon) {
        error = StrUtils.wrapInHtml(error);
        JOptionPane optionPane = new JOptionPane(error);
        optionPane.setIcon(icon);
        JDialog dialog = optionPane.createDialog(title);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        dialog.dispose();
//        JOptionPane.showMessageDialog(parent, error, title, JOptionPane.ERROR_MESSAGE, icon);
    }

    public static DialogOption showListDialog(Component parent, String message, String title, ImageIcon icon, DialogOption[] options) {
        message = StrUtils.wrapInHtml(message);
        JOptionPane jOptionPane = new JOptionPane(new MyLbl(message), JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, icon, options);
        JDialog dialog = jOptionPane.createDialog(parent, title);

        for (DialogOption dialogOption : options) {
            dialogOption.addActionListener(src -> {
                jOptionPane.setValue(dialogOption);
                dialog.dispose();
            });
        }
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);  // WILL BLOCK the program
        dialog.dispose();   // close dialog

        return (DialogOption) jOptionPane.getValue();
    }

   
    public static class DialogOption extends JButton {
        private final Object key;
        private ImageIcon icon = null;
        private String str = null;

        /**
         * key = str
         *
         * @param str
         */
        public DialogOption(String str) {
            this.str = str;
            this.key = str;
            setText(str);
        }

        public DialogOption(ImageIcon icon, Object key) {
            this.icon = icon;
            this.key = key;
            setIcon(icon);
        }

        public Object getKey() {
            return key;
        }

        public String getStr() {
            return str;
        }
    }

}
