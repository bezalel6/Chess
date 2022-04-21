package ver14.SharedClasses.UI;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.UI.dialogs.ConfirmDialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

import static java.awt.event.KeyEvent.*;

public class MyJFrame extends JFrame {
    private static final int delayInMs = 100;

    private final MyAdapter myAdapter;
    private boolean isSleeping = false;
    private boolean confirmExit = false;
    private VoidCallback onClose = () -> {
    };

    public MyJFrame() throws HeadlessException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.myAdapter = debugAdapter(this);
        myAdapter.addAction(() -> {
            setExtendedState(getExtendedState() ^ JFrame.MAXIMIZED_BOTH);
        }, VK_F11);
    }

    public static MyAdapter debugAdapter(Window addTo) {

        MyAdapter adapter = new MyAdapter() {{
            addAction(() -> {
                addBorderRec(addTo);
            }, VK_CONTROL, VK_SHIFT, VK_D);

            addAction(addTo::pack, VK_CONTROL, VK_SHIFT, VK_P);
        }};

        addTo.addKeyListener(adapter);
        return adapter;
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

    public MyAdapter getMyAdapter() {
        return myAdapter;
    }

    public void setOnExit(VoidCallback onClose) {
        setOnExit(true, onClose);
    }

    public void setOnExit(boolean confirmExit, VoidCallback onClose) {
        this.confirmExit = confirmExit;
        this.onClose = onClose;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                doXClick();
            }
        });
    }

    public void doXClick() {
        if (!confirmExit || ConfirmDialogs.confirm(MyJFrame.this, "Exit Confirmation", "Are You Sure You Want To Exit?", null))
            onClose.callback();
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

    public static class MyAdapter extends KeyAdapter {
        private static final long coolDown = 1000;
        private final Set<Integer> pressedKeys = new HashSet<>();
        private final Map<Set<Integer>, VoidCallback> actions = new HashMap<>();
        private Integer lastPressedKey = null;
        private long lastPressedTime = 0;

        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            pressedKeys.add(e.getKeyCode());
            if (lastPressedKey != null && e.getKeyCode() == lastPressedKey && System.currentTimeMillis() - lastPressedTime <= coolDown) {
                return;
            }
            lastPressedTime = System.currentTimeMillis();
            lastPressedKey = e.getKeyCode();
            actions.forEach((set, callback) -> {
                if (pressedKeys.containsAll(set))
                    callback.callback();
            });

        }

        @Override
        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            pressedKeys.remove(e.getKeyCode());
            lastPressedKey = e.getKeyCode() == lastPressedKey ? null : lastPressedKey;
        }

        public Set<Integer> addAction(VoidCallback action, Integer... keys) {
            Set<Integer> ret = new HashSet<>(List.of(keys));
            actions.put(ret, action);
            return ret;
        }


        public void removeAction(Set<Integer> action) {
            actions.remove(action);
        }

    }
}
