package ver14.SharedClasses.ui.windows;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.ui.dialogs.ConfirmDialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MyJFrame extends JFrame {
    private static final int delayInMs = 100;

    private boolean isSleeping = false;

    public MyJFrame() throws HeadlessException {
    }

    public void setOnExit(VoidCallback onClose) {
        setOnExit(true, onClose);
    }

    public void setOnExit(boolean confirmExit, VoidCallback onClose) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!confirmExit || ConfirmDialogs.confirm(MyJFrame.this, "Exit Confirmation", "Are You Sure You Want To Exit?", null))
                    onClose.callback();
            }
        });
    }

    public void setOnResize(VoidCallback onResize) {
        getRootPane().addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if (isSleeping)
                    return;
                isSleeping = true;

                new Thread(() -> {
                    try {
                        Thread.sleep(delayInMs);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    onResize.callback();
                    isSleeping = false;
                }).start();
            }
        });
    }
}
