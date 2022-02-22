package ver5.SharedClasses.ui.windows;

import ver5.SharedClasses.Callback;
import ver5.SharedClasses.ui.dialogs.ConfirmDialogs;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CloseConfirmationJFrame extends JFrame {
    private static final int delayInMs = 100;
    private long lastResize;
    private boolean leave = false;

    public CloseConfirmationJFrame(Callback onClose) {
        this(onClose, null);
    }

    public CloseConfirmationJFrame(Callback onClose, Callback onResize) {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (ConfirmDialogs.confirm(CloseConfirmationJFrame.this, "Exit Confirmation", "Are You Sure You Want To Exit?", null))
                    onClose.callback();
            }
        });

        if (onResize != null) {
            getRootPane().addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    if (leave)
                        return;
                    leave = true;
                    lastResize = System.currentTimeMillis();
                    new Thread(() -> {
                        while (System.currentTimeMillis() - lastResize < delayInMs) ;
                        onResize.callback();
                        leave = false;
                    }).start();
                }
            });
        }
    }
}
