package ver14.SharedClasses.UI;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.UI.dialogs.ConfirmDialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.awt.event.KeyEvent.*;


/**
 * The type My j frame.
 */
public class MyJFrame extends JFrame {
    private static final int resizeDelayInMs = 100;

    private final MyAdapter myAdapter;
    /**
     * The On close.
     */
    protected Closing<?> onClose = null;

    /**
     * Instantiates a new My j frame.
     *
     * @throws HeadlessException the headless exception
     */
    public MyJFrame() throws HeadlessException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.myAdapter = debugAdapter(this);
        myAdapter.addAction(this::toggleFullscreen, VK_F11);
    }

    /**
     * Debug adapter my adapter.
     *
     * @param addTo the add to
     * @return the my adapter
     */
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

    public void toggleFullscreen() {

        setExtendedState(getExtendedState() ^ JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Gets my adapter.
     *
     * @return the my adapter
     */
    public MyAdapter getMyAdapter() {
        return myAdapter;
    }


    /**
     * Sets on exit.
     *
     * @param onClose the on close
     */
    public void setOnExit(Closing<?> onClose) {
        this.onClose = onClose;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                doXClick();
            }
        });
    }

    /**
     * Do x click.
     */
    public void doXClick() {
        if (onClose != null)
            onClose.tryClose();
    }

    /**
     * Sets on resize.
     *
     * @param onResize the on resize
     */
    public void setOnResize(VoidCallback onResize) {
        addResizeEvent(getRootPane(), onResize);
    }

    public static void addResizeEvent(JRootPane rootPane, VoidCallback onResize) {
        final boolean[] isSleeping = {false};
        ExecutorService executor = Executors.newSingleThreadExecutor();
        rootPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if (isSleeping[0])
                    return;
                isSleeping[0] = true;

                executor.submit(() -> {
                    try {
                        Thread.sleep(resizeDelayInMs);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    onResize.callback();
                    isSleeping[0] = false;
                });
            }
        });
    }

    /**
     * The interface Closing.
     *
     * @param <T> the type parameter
     */
    public interface Closing<T> {
        /**
         * The constant title.
         */
        String title = "Exit Confirmation";
        /**
         * The constant header.
         */
        String header = "Are You Sure You Want To Exit?";
        /**
         * The constant icon.
         */
        ImageIcon icon = null;

        /**
         * Try close.
         */
        default void tryClose() {
            T t = show();
            if (checkClosingVal(t))
                closing(t);
        }

        /**
         * Show t.
         *
         * @return the t
         */
        T show();

        /**
         * Check closing val boolean.
         *
         * @param val the val
         * @return the boolean
         */
        boolean checkClosingVal(T val);

        /**
         * Closing.
         *
         * @param val the val
         */
        void closing(T val);
    }

    /**
     * The interface Boolean closing.
     */
    public interface BooleanClosing extends Closing<Boolean> {

        /**
         * Show boolean.
         *
         * @return the boolean
         */
        default Boolean show() {
            return ConfirmDialogs.confirm(null, title, header, icon);
        }

        ;

        /**
         * Check closing val boolean.
         *
         * @param val the val
         * @return the boolean
         */
        @Override
        default boolean checkClosingVal(Boolean val) {
            return val;
        }

        @Override
        default void closing(Boolean val) {
            if (val)
                closing();
        }

        /**
         * Closing.
         */
        void closing();
    }

    /**
     * The interface String closing.
     */
    public interface StringClosing extends Closing<String> {

        /**
         * Show string.
         *
         * @return the string
         */
        default String show() {
            return ConfirmDialogs.confirm(null, title, header, "Enter closing reason", icon, initialValue());
        }

        /**
         * Check closing val boolean.
         *
         * @param val the val
         * @return the boolean
         */
        @Override
        default boolean checkClosingVal(String val) {
            return val != null;
        }

        String initialValue();
    }


    /**
     * The type My adapter.
     */
    public static class MyAdapter extends KeyAdapter {
        private static final long coolDown = 1000;
        private final Set<Integer> pressedKeys = new HashSet<>();
        private final Map<Integer, HeldDown> heldDownMap = new HashMap<>();
        private final Map<Set<Integer>, VoidCallback> actions = new HashMap<>();
        private Integer lastPressedKey = null;
        private long lastPressedTime = 0;

        /**
         * Key pressed.
         *
         * @param e the e
         */
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            if (!pressedKeys.contains(e.getKeyCode()) && heldDownMap.containsKey(e.getKeyCode())) {
                heldDownMap.get(e.getKeyCode()).startPress();
            }
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

        /**
         * Key released.
         *
         * @param e the e
         */
        @Override
        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            if (pressedKeys.contains(e.getKeyCode()) && heldDownMap.containsKey(e.getKeyCode())) {
                heldDownMap.get(e.getKeyCode()).endPress();
            }
            pressedKeys.remove(e.getKeyCode());
            lastPressedKey = (lastPressedKey != null && e.getKeyCode() == lastPressedKey) ? null : lastPressedKey;
        }

        /**
         * Add held down.
         *
         * @param heldDown the held down
         */
        public void addHeldDown(HeldDown heldDown) {
            heldDownMap.put(heldDown.key(), heldDown);
        }

        /**
         * Add action set.
         *
         * @param action the action
         * @param keys   the keys
         * @return the set
         */
        public Set<Integer> addAction(VoidCallback action, Integer... keys) {
            Set<Integer> ret = new HashSet<>(List.of(keys));
            actions.put(ret, action);
            return ret;
        }

        /**
         * Remove action.
         *
         * @param action the action
         */
        public void removeAction(Set<Integer> action) {
            actions.remove(action);
        }


        /**
         * The interface Held down.
         */
        public interface HeldDown {
            /**
             * Start press.
             */
            void startPress();

            /**
             * End press.
             */
            void endPress();

            /**
             * Key int.
             *
             * @return the int
             */
            int key();
        }

    }
}
