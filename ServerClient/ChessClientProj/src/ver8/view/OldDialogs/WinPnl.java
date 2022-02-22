package ver8.view.OldDialogs;

import ver8.SharedClasses.FontManager;
import ver8.SharedClasses.ui.MyLbl;
import ver8.view.IconManager;
import ver8.view.List.ListItem;
import ver8.view.List.OldComponentsList;
import ver8.view.OldDialogs.Navigation.BackOkInterface;
import ver8.view.OldDialogs.Navigation.BackOkPnl;

import javax.swing.*;
import java.awt.*;

public class WinPnl extends JPanel implements OldComponentsList.Container {
    public final static Insets insets = new Insets(5, 5, 5, 5);
    public static final IconManager.Size listSize = new IconManager.Size(250);
    public static final IconManager.Size listItemSize = new IconManager.Size(listSize) {{
        multBy(0.7);
    }};

    public final Header header;
    private final int cols;
    private JPanel headerPnl;
    private int topPnlRow = 0;
    private int currentRow = 0;
    private int currentCol = 0;
    private BackOkPnl backOkPnl;

    public WinPnl(ImageIcon icon) {
        this(icon, true);
    }

    public WinPnl(ImageIcon icon, boolean centerHeader) {
        this(1, new Header(icon, centerHeader), centerHeader);
    }

    public WinPnl(int cols, Header header, boolean centerHeader) {
        super(new GridBagLayout());
        this.cols = cols;
        this.header = header;
        topPnlSetup(centerHeader, header);
    }

    private void topPnlSetup(boolean centerHeader, Header header) {
        this.headerPnl = new JPanel() {{
            setLayout(new GridBagLayout());
        }};
        MyLbl lbl = new MyLbl(header.getTxt(), header.getIcon(), SwingConstants.CENTER) {{
            setFont(FontManager.loginProcess);
        }};
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 2;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        if (!centerHeader)
            gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = insets;

        this.headerPnl.add(lbl, gbc);
        topPnlRow++;

        add(this.headerPnl, false, new GridBagConstraints() {{
            anchor = PAGE_START;
        }});
        currentRow++;
        currentCol = 0;
    }

    public void add(Component comp, boolean useInsets, GridBagConstraints gbc) {
        add(comp, useInsets, gbc, false);
    }

    public void add(Component comp, boolean useInsets, GridBagConstraints gbc, boolean isList) {
        if (gbc == null) gbc = new GridBagConstraints();

        if (useInsets) {
            gbc.insets = insets;
            gbc.gridx = currentCol++;
            gbc.gridy = currentRow;
            if (currentCol == cols) {
                currentRow++;
                currentCol = 0;
            }
        } else {
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
        }
        if (isList) {
            comp = new JScrollPane(comp) {{
                setPreferredSize(listSize);
                setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            }};
        }
        add(comp, gbc);
    }

    public WinPnl(String header) {
        this(header, true);
    }


    public WinPnl(String header, boolean centerHeader) {
        this(1, new Header(header, true), centerHeader);
    }

    public void initBackOk(BackOkInterface backOkInterface) {
        backOkPnl = new BackOkPnl(backOkInterface);
    }

    protected void addToTopPnl(Component comp, boolean center) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 2;
        gbc.gridy = topPnlRow++;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        if (!center)
            gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = insets;

        headerPnl.add(comp, gbc);
    }

//    public void addFill(Component comp) {
//        add(comp, false, new GridBagConstraints() {{
//            weightx = 1;
//        }});
//    }

    public BackOkPnl getBackOkPnl() {
        return backOkPnl;
    }

    public void doneAdding() {
        if (backOkPnl != null)
            add(backOkPnl);
    }

    @Override
    public Component add(Component comp) {
        add(comp, true, null);
        return null;
    }

    public void addList(OldComponentsList comp) {
        add(comp.listContainer().container(), true, null);
    }

    public void addNav(OldComponentsList comp) {
        add(comp.navigationComp(), true, null);
    }

    @Override
    public void removeAllListItems() {
        removeAll();
    }

    @Override
    public void add(ListItem item, OldComponentsList list) {
        add(item.comp(), list.isVertical());
    }

    @Override
    public JComponent container() {
        return this;
    }

    @Override
    public void updated() {
        repaint();
    }

    public void add(Component comp, boolean isList) {
        add(comp, true, null, isList);
    }
}
