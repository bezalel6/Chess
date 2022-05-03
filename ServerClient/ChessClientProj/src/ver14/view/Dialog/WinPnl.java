package ver14.view.Dialog;

import ver14.view.Dialog.DialogFields.DialogField;
import ver14.view.Dialog.Dialogs.Header;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Win pnl - my implementation of a panel. with a customizable layout that generally works similarly to a {@link GridLayout}, but allows for
 * custom settings like a {@link GridBagLayout}
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class WinPnl extends JPanel {
    /**
     * The constant insets.
     */
    public final static Insets insets = new Insets(5, 5, 5, 5);

    /**
     * The constant ALL_IN_ONE_ROW.
     */
    public static final int ALL_IN_ONE_ROW = -1;//used so currentCol will never be equal to cols
    /**
     * The constant MAKE_SCROLLABLE.
     */
    public static final int MAKE_SCROLLABLE = -2;
    /**
     * The Top pnl.
     */
    protected final JPanel topPnl;
    /**
     * The Bottom pnl.
     */
    protected final JPanel bottomPnl;
    /**
     * The Content pnl.
     */
    protected final JComponent contentPnl;
    /**
     * The Header.
     */
    protected Header header;
    /**
     * The My insets.
     */
    private Insets myInsets = insets;
    /**
     * The Cols.
     */
    private int cols;
    /**
     * The Current row.
     */
    private int currentRow;
    /**
     * The Current col.
     */
    private int currentCol;

    /**
     * Instantiates a new Win pnl.
     */
    public WinPnl() {
        this((Header) null);
    }

    /**
     * Instantiates a new Win pnl.
     *
     * @param header the header
     */
    public WinPnl(Header header) {
        this(1, header);
    }


    /**
     * Instantiates a new Win pnl.
     *
     * @param cols   the cols
     * @param header the header
     */
    public WinPnl(int cols, Header header) {
        super(new BorderLayout());
        this.cols = cols;

        this.topPnl = new JPanel();
        this.topPnl.setLayout(new BoxLayout(topPnl, BoxLayout.Y_AXIS));
        setHeader(header);

        this.contentPnl = new JPanel(new GridBagLayout());

        if (cols == MAKE_SCROLLABLE) {
//            super.add(new Scrollable(contentPnl), BorderLayout.CENTER);
            super.add(contentPnl, BorderLayout.CENTER);
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

    /**
     * Reset grid counters.
     */
    private void resetGridCounters() {
        currentCol = currentRow = 0;
    }

    /**
     * Instantiates a new Win pnl.
     *
     * @param cols the cols
     */
    public WinPnl(int cols) {
        this(cols, null);
    }

    /**
     * Instantiates a new Win pnl.
     *
     * @param header the header
     */
    public WinPnl(String header) {
        this(header, true);
    }

    /**
     * Instantiates a new Win pnl.
     *
     * @param header       the header
     * @param centerHeader the center header
     */
    public WinPnl(String header, boolean centerHeader) {
        this(1, new Header(header, centerHeader));
    }

    /**
     * Sets insets.
     *
     * @param myInsets the my insets
     */
    public void setInsets(Insets myInsets) {
        this.myInsets = myInsets;
    }

    /**
     * Sets border.
     */
    public void setBorder() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    /**
     * Gets header.
     *
     * @return the header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Sets header.
     *
     * @param header the header
     */
    public void setHeader(Header header) {
        if (this.header != null)
            topPnl.remove(this.header);
        this.header = header;
        if (header != null)
            topPnl.add(header, 0);
        topPnl.revalidate();
        topPnl.repaint();
    }

    /**
     * Sets cols.
     *
     * @param cols the cols
     */
    public void setCols(int cols) {
        this.cols = cols;
    }

    /**
     * Add in component in a new line.
     *
     * @param comp the comp
     */
    public void addInNewLine(Component comp) {
        nextLine();
        add(comp);
    }

    /**
     * Next line.
     */
    protected void nextLine() {
        currentRow++;
        currentCol = 0;
    }

    @Override
    public Component add(Component comp) {
        add(comp, null);
        return comp;
    }

    /**
     * Add a component with custom settings.
     *
     * @param comp the comp
     * @param gbc  the gbc
     */
    public void add(Component comp, GridBagConstraints gbc) {
        if (gbc == null) gbc = new GridBagConstraints();
        gbc.insets = myInsets;
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

        contentPnl.add(comp, gbc);
    }

    /**
     * Add all.
     *
     * @param comps the comps
     */
    public void addAll(Component... comps) {
        Arrays.stream(comps).forEach(this::add);
    }

    /**
     * Remove content component.
     *
     * @param comp the comp
     */
    public void removeContentComponent(Component comp) {
        contentPnl.remove(comp);
    }

    /**
     * Remove content.
     */
    public void removeContent() {
        resetGridCounters();
        contentPnl.removeAll();
    }

}
