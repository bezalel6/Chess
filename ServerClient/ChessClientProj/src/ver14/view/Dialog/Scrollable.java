package ver14.view.Dialog;

import javax.swing.*;
import java.awt.*;

/**
 * my implementation of a Scrollable component.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Scrollable extends JScrollPane {
    /**
     * The Og comp.
     */
    private final JComponent ogComp;
    /**
     * The Just scrolled.
     */
    private boolean justScrolled = false;

    /**
     * Instantiates a new Scrollable.
     */
    public Scrollable() {
        this(null);
    }

    /**
     * Instantiates a new Scrollable.
     *
     * @param ogComp the og comp
     */
    public Scrollable(JComponent ogComp) {
        this(ogComp, null);
    }

    /**
     * Instantiates a new Scrollable.
     *
     * @param ogComp the og comp
     * @param size   the size
     */
    public Scrollable(JComponent ogComp, Dimension size) {
        super(ogComp);
        this.ogComp = ogComp;
        applyDefaults(this, size == null ? ogComp.getPreferredSize() : size);
        scrollToTop();
    }

    /**
     * Apply default settings to a jscrollpane.
     *
     * @param scrollPane the scroll pane
     * @param size       the size
     */
    public static void applyDefaults(JScrollPane scrollPane, Dimension size) {
        SwingUtilities.invokeLater(() -> {
            try {
                scrollPane.setSize(size);
                scrollPane.setPreferredSize(size);
//                scrollPane.setMaximumSize(size);

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        });
        int inc = 100;
        scrollPane.getVerticalScrollBar().setBlockIncrement(inc);
        scrollPane.getVerticalScrollBar().setUnitIncrement(inc);
        scrollPane.getHorizontalScrollBar().setBlockIncrement(inc);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(inc);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    }

    /**
     * Scroll to top.
     */
    public void scrollToTop() {
        scrollTo(0);
    }

    /**
     * Scroll to.
     *
     * @param position the position
     */
    private void scrollTo(int position) {
        SwingUtilities.invokeLater(() -> {
            verticalScrollBar.revalidate();
            verticalScrollBar.setValue(position);
        });
    }

    /**
     * My set size.
     *
     * @param size the size
     */
    public void mySetSize(Dimension size) {
        SwingUtilities.invokeLater(() -> {
            try {
                setSize(size);
                setPreferredSize(size);
//                setMaximumSize(size);

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        });
    }

    /**
     * Auto scroll to bottom scroll notifier.
     *
     * @return the scroll notifier
     */
    public ScrollNotifier autoScrollToBottom() {
        verticalScrollBar.addAdjustmentListener(l -> {
            if (!justScrolled) {
                scrollToBottom();
                justScrolled = true;
            }
        });
        return () -> {
            justScrolled = false;
        };
    }

    /**
     * Scroll to bottom.
     */
    public void scrollToBottom() {
        scrollTo(verticalScrollBar.getMaximum());
    }

    /**
     * Add to component.
     *
     * @param adding      the adding
     * @param constraints the constraints
     */
    public void addToComponent(Component adding, Object constraints) {
        ogComp.add(adding, constraints);
    }

    /**
     * Scroll notifier.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public interface ScrollNotifier {
        /**
         * Scroll.
         */
        void scroll();
    }


}
