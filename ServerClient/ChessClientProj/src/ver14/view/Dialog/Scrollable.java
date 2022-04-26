package ver14.view.Dialog;

import javax.swing.*;
import java.awt.*;

public class Scrollable extends JScrollPane {
    private final JComponent ogComp;
    private boolean justScrolled = false;

    public Scrollable() {
        this(null);
    }

    public Scrollable(JComponent ogComp) {
        this(ogComp, null);
    }

    public Scrollable(JComponent ogComp, Dimension size) {
        super(ogComp);
        this.ogComp = ogComp;
        applyDefaults(this, size == null ? ogComp.getPreferredSize() : size);
        scrollToTop();
    }

    public static void applyDefaults(JScrollPane scrollPane, Dimension size) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("applying " + size);
                scrollPane.setSize(size);
                scrollPane.setPreferredSize(size);
                scrollPane.setMaximumSize(size);

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        });

        scrollPane.getVerticalScrollBar().setBlockIncrement(100);
        scrollPane.getVerticalScrollBar().setUnitIncrement(100);
        scrollPane.getHorizontalScrollBar().setBlockIncrement(100);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(100);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    }

    public void scrollToTop() {
        scrollTo(0);
    }

    private void scrollTo(int position) {
        System.out.println("scrolling to " + position);
        SwingUtilities.invokeLater(() -> {
            verticalScrollBar.revalidate();
            verticalScrollBar.setValue(position);
        });
    }

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

    public void scrollToBottom() {
        scrollTo(verticalScrollBar.getMaximum());
    }

    public void addToComponent(Component adding, Object constraints) {
        ogComp.add(adding, constraints);
    }

    public interface ScrollNotifier {
        void scroll();
    }


}
