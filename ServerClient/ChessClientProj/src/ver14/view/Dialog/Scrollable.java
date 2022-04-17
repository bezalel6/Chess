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
        SwingUtilities.invokeLater(() -> {
            Dimension s = size;
            try {
                s = s == null ? getPreferredSize() : s;
                setSize(s);
            } catch (Exception e) {
                System.out.println(e);
                return;
            }
            setPreferredSize(s);
            setMaximumSize(s);
        });
        getVerticalScrollBar().setBlockIncrement(100);
        getVerticalScrollBar().setUnitIncrement(100);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollToTop();
    }

    public void scrollToTop() {
        scrollTo(0);
    }

    private void scrollTo(int position) {
        SwingUtilities.invokeLater(() -> {
            verticalScrollBar.revalidate();
            verticalScrollBar.setValue(position);
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
