package ver26_save_minimax_levels.view_classes.dialogs_classes;

import ver26_save_minimax_levels.view_classes.dialogs_classes.dialog_objects.DialogButton;
import ver26_save_minimax_levels.view_classes.dialogs_classes.dialog_objects.DialogObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class Dialogs extends JDialog implements ActionListener {
    public static final Object OK = 0, CANCEL = 1;
    JPanel bottomPnl, bodyPnl;
    Object result;
    private Object cancelKey = CANCEL;

    public Dialogs(String title) {
        super((Frame) null, title, true);
        setLocationRelativeTo(null);
        bottomPnl = new JPanel();
        bodyPnl = new JPanel();
        setBottomPnl();
        setAlwaysOnTop(true);
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
//todo make it not infinite
                YesOrNo confirm = new YesOrNo("Exit Confirmation", "Are You Sure You Want To Exit?");
                int res = (int) confirm.run();
                if (res == YesOrNo.YES) {
                    result = cancelKey;
                }
            }
        };
        addWindowListener(exitListener);
    }

    static DialogButton createOkButton() {
        return new DialogButton(OK, "Ok");
    }

    GridLayout createGridLayout(int r, int c) {
        return new GridLayout(r, c, 2, 2);
    }

    DialogButton createCancelButton() {
        return new DialogButton(cancelKey, "Cancel");
    }

    public void setCancelKey(Object cancelKey) {
        this.cancelKey = cancelKey;
        bottomPnl.removeAll();
        setBottomPnl();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        result = ((DialogObject) e.getSource()).getKey();
        dispose();
    }

    public abstract Object run();

    abstract void setBottomPnl();

    private void setListener(JPanel pnl) {
        for (Component component : pnl.getComponents()) {
            if (component instanceof DialogButton)
                ((DialogButton) component).addActionListener(this);
            else if (component instanceof JFormattedTextField)
                ((JFormattedTextField) component).addActionListener(this);
        }
    }

    Object runDialog() {
        LayoutManager lm = createGridLayout(2, 1);
        getContentPane().setLayout(lm);
        setListener(bodyPnl);
        setListener(bottomPnl);
        getContentPane().add(bodyPnl);
        getContentPane().add(bottomPnl);
        pack();
        this.setVisible(true);
        return result;
    }
}
