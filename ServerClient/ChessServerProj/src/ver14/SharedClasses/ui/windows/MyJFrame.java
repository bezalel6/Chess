package ver14.SharedClasses.ui.windows;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.ui.dialogs.ConfirmDialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class MyJFrame extends JFrame {
    private static final int delayInMs = 100;

    private boolean isSleeping = false;

    public MyJFrame() throws HeadlessException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        debugAdapter(this);
    }

    public static void debugAdapter(Window addTo) {
        final ArrayList<Integer> enableOn = new ArrayList<>() {{
            add(KeyEvent.VK_CONTROL);
            add(KeyEvent.VK_SHIFT);
            add(KeyEvent.VK_D);
        }};
        final ArrayList<Integer> packOn = new ArrayList<>() {{
            add(KeyEvent.VK_CONTROL);
            add(KeyEvent.VK_SHIFT);
            add(KeyEvent.VK_P);
        }};
        final ArrayList<Integer> rndOn = new ArrayList<>() {{
            add(KeyEvent.VK_CONTROL);
            add(KeyEvent.VK_SHIFT);
            add(KeyEvent.VK_R);
        }};
        Vector<Integer> pressed = new Vector<>();
        KeyAdapter adapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                pressed.add(e.getKeyCode());
                if (enableOn.stream().noneMatch(enable -> pressed.stream().noneMatch(press -> Objects.equals(press, enable)))) {
                    addBorderRec(addTo);
                }
                if (packOn.stream().noneMatch(enable -> pressed.stream().noneMatch(press -> Objects.equals(press, enable)))) {
                    addTo.pack();
                }
                if (addTo instanceof MyJFrame myJFrame && rndOn.stream().noneMatch(enable -> pressed.stream().noneMatch(press -> Objects.equals(press, enable)))) {
                    myJFrame.onRnd();
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                pressed.remove((Integer) e.getKeyCode());
            }
        };
        addTo.addKeyListener(adapter);
    }

    private static void addBorderRec(Container container) {
        if (container instanceof JComponent component && !(container instanceof JViewport)) {
            component.setBorder(BorderFactory.createLineBorder(Color.orange));
            component.setToolTipText(component.toString());
//            component.add(new JLabel(component.toString()));
        }

        for (Component component : container.getComponents()) {
            if (component instanceof JComponent jComponent) {
                addBorderRec(jComponent);
            }
        }
    }

    protected void onRnd() {

    }

    public void setOnExit(VoidCallback onClose) {
        setOnExit(true, onClose);
    }

    public void setOnExit(boolean confirmExit, VoidCallback onClose) {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
