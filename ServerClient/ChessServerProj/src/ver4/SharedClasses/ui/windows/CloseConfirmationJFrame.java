package ver4.SharedClasses.ui.windows;

import ver4.SharedClasses.ui.dialogs.ConfirmDialogs;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CloseConfirmationJFrame extends JFrame {
    public CloseConfirmationJFrame(Callback onClose) {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (ConfirmDialogs.confirm(CloseConfirmationJFrame.this, "Exit Confirmation", "Are You Sure You Want To Exit?", null))
                    onClose.callback();
            }
        });
    }
}
