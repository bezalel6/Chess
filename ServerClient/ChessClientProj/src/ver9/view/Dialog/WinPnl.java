package ver9.view.Dialog;

import ver9.view.Dialog.Dialogs.Header;
import ver9.view.IconManager;

import javax.swing.*;
import java.awt.*;

public class WinPnl extends JPanel {
    public final static Insets insets = new Insets(5, 5, 5, 5);
    public static final IconManager.Size listSize = new IconManager.Size(250);
    public static final IconManager.Size listItemSize = new IconManager.Size(listSize) {{
        multBy(0.7);
    }};
    public static final int ALL_IN_ONE_ROW = -1;//used so currentCol will never be equal to cols
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

        this.contentPnl = new JPanel(new GridBagLayout());
        if (cols == MAKE_SCROLLABLE) {
            super.add(new Scrollable(contentPnl), BorderLayout.CENTER);
            this.cols = 1;
        } else {
            super.add(contentPnl, BorderLayout.CENTER);
        }

        this.bottomPnl = new JPanel(new GridLayout(0, 1));
        topPnl.add(header);

        super.add(topPnl, BorderLayout.NORTH);
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

    public void add(Component comp, boolean useInsets, GridBagConstraints gbc, boolean... _makeScrollable) {
        if (gbc == null) gbc = new GridBagConstraints();
        boolean makeScrollable = _makeScrollable.length > 0 && _makeScrollable[0];
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
        if (makeScrollable) {
            comp = new Scrollable((JComponent) comp);
        }
        contentPnl.add(comp, gbc);
    }

    protected void removeContentComponent(Component comp) {
        contentPnl.remove(comp);
    }

    public void add(Component comp, boolean makeScrollable) {
        add(comp, true, null, makeScrollable);
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
