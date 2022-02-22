package ver10.view.Dialog;

import ver10.view.Dialog.DialogFields.DialogField;
import ver10.view.Dialog.Dialogs.Header;
import ver10.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;

public class WinPnl extends JPanel {
    public final static Insets insets = new Insets(5, 5, 5, 5);
    public static final Size listSize = new Size(250);
    public static final Size listItemSize = new Size(listSize) {{
        multBy(0.7);
    }};
    public static final int ALL_IN_ONE_ROW = -1;//used so currentCol will never be equal to cols
    public static final int MAKE_SCROLLABLE = -2;
    protected final JPanel topPnl;
    protected final JPanel bottomPnl;
    private final JComponent contentPnl;
    protected Header header;
    private int cols;
    private int currentRow;
    private int currentCol;

    public WinPnl() {
        this((Header) null);
    }

    public WinPnl(Header header) {
        this(1, header);
    }

    public WinPnl(int cols, Header header) {
        super(new BorderLayout());
        this.cols = cols;

        this.topPnl = new JPanel();
        this.topPnl.setLayout(new BoxLayout(topPnl, BoxLayout.Y_AXIS));
        setHeader(header);

        this.contentPnl = new JPanel(new GridBagLayout());
        if (cols == MAKE_SCROLLABLE) {
            super.add(new Scrollable(contentPnl), BorderLayout.CENTER);
            this.cols = 1;
        } else {
            super.add(contentPnl, BorderLayout.CENTER);
        }

        this.bottomPnl = new JPanel();
        this.bottomPnl.setLayout(new BoxLayout(bottomPnl, BoxLayout.Y_AXIS));

        super.add(topPnl, BorderLayout.NORTH);
        super.add(bottomPnl, BorderLayout.SOUTH);

        resetGridCounters();
    }

    private void resetGridCounters() {
        currentCol = currentRow = 0;
    }

    public WinPnl(String header) {
        this(header, true);
    }

    public WinPnl(String header, boolean centerHeader) {
        this(1, new Header(header, centerHeader));
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        if (this.header != null)
            topPnl.remove(this.header);
        this.header = header;
        if (header != null)
            topPnl.add(header, 0);
        topPnl.revalidate();
        topPnl.repaint();

    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void addInNewLine(Component comp) {
        nextLine();
        add(comp);
    }

    protected void nextLine() {
        currentRow++;
        currentCol = 0;
    }

    @Override
    public Component add(Component comp) {
        add(comp, null);
        return null;
    }

    public void add(Component comp, GridBagConstraints gbc) {
        if (gbc == null) gbc = new GridBagConstraints();
        gbc.insets = insets;
        gbc.gridx = currentCol++;
        gbc.gridy = currentRow;
        if (cols != ALL_IN_ONE_ROW && currentCol == cols) {
            nextLine();
        }
//fill horizontally
        if (comp instanceof DialogField) {
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 2;
        }

        if (comp instanceof ScrollableComponent) {
            comp = new Scrollable((JComponent) comp);
        }
        contentPnl.add(comp, gbc);
    }

    protected void removeContentComponent(Component comp) {
        contentPnl.remove(comp);
    }

    public void removeContent() {
        resetGridCounters();
        contentPnl.removeAll();
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
