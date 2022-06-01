package ver14.SharedClasses.UI.MyJframe;

import ver14.SharedClasses.Callbacks.VoidCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.awt.event.KeyEvent.*;


/**
 * represents my implementation of a window.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MyJFrame extends JFrame {
    /**
     * The constant resizeDelayInMs.
     */
    private static final int resizeDelayInMs = 100;

    /**
     * The My adapter.
     */
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

    /**
     * Add border rec.
     *
     * @param container the container
     */
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

    /**
     * Toggle fullscreen.
     */
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

    /**
     * Add a callback to call on a resize of a root pane. when a resize event
     * is invoked,  a delay of {@value #resizeDelayInMs}ms will limit the number of unnecessary calls
     * while a resize is taking place, and will offer a good tradeoff between snappiness, and performance.
     *
     * @param rootPane the root pane to add the callback to.
     * @param onResize the callback to call on a resize.
     */
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


}
