package ver8.view.Dialog;

import ver8.view.IconManager;
import ver8.view.OldDialogs.Header;

import javax.swing.*;
import java.awt.*;

public class WinPnl extends JPanel {
    public final static Insets insets = new Insets(5, 5, 5, 5);
    public static final IconManager.Size listSize = new IconManager.Size(250);
    public static final IconManager.Size listItemSize = new IconManager.Size(listSize) {{
        multBy(0.7);
    }};
    public static final int ALL_IN_ONE_ROW = -1;
    public static final int MAKE_SCROLLABLE = -2;
    public final Header header;
    protected final JPanel topPnl;
    protected final JPanel bottomPnl;
    private final JComponent contentPnl;
    private int cols;
    private int currentRow = 0;
    private int currentCol = 0;

    public WinPnl(Header header) {
        this(1, header);
    }

    public WinPnl(int cols, Header header) {
        super(new BorderLayout());
        this.cols = cols;
        this.header = header;

        this.topPnl = new JPanel(new GridLayout(0, 1));

        JComponent tContent = new JPanel(new GridBagLayout());
        if (cols == MAKE_SCROLLABLE) {
            tContent = new Scrollable(tContent);
            this.cols = 1;
        }
        this.contentPnl = tContent;

        this.bottomPnl = new JPanel(new GridLayout(0, 1));
        topPnl.add(header);

        super.add(topPnl, BorderLayout.NORTH);
        super.add(contentPnl, BorderLayout.CENTER);
        super.add(bottomPnl, BorderLayout.SOUTH);

        currentRow = 0;
        currentCol = 0;
    }


    public WinPnl(String header) {
        this(header, true);
    }

    public WinPnl(String header, boolean centerHeader) {
        this(1, new Header(header, centerHeader));
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    @Override
    public Component add(Component comp) {
        add(comp, true, null);
        return null;
    }

    public void add(Component comp, boolean useInsets, GridBagConstraints gbc, boolean... makeScrollable) {
        if (gbc == null) gbc = new GridBagConstraints();
        boolean isList = makeScrollable.length > 0 && makeScrollable[0];
        if (useInsets) {
            gbc.insets = insets;
            gbc.gridx = currentCol++;
            gbc.gridy = currentRow;
            if (cols != ALL_IN_ONE_ROW && currentCol == cols) {
                currentRow++;
                currentCol = 0;
            }
        } else {
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
        }
        if (isList) {
            comp = new Scrollable((JComponent) comp);
        }
        if (contentPnl instanceof Scrollable scrollable) {
            scrollable.addToComponent(comp, gbc);
        } else {
            contentPnl.add(comp, gbc);
        }
    }

    public void add(Component comp, boolean isList) {
        add(comp, true, null, isList);
    }

    public static class Scrollable extends JScrollPane {
        private final JComponent ogComp;

        public Scrollable(JComponent ogComp) {
            super(ogComp);
            this.ogComp = ogComp;
            setPreferredSize(listSize);
            setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            getVerticalScrollBar().setUnitIncrement(10);
        }

        public void addToComponent(Component adding, Object constraints) {
            ogComp.add(adding, constraints);
        }


    }
}
