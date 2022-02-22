package ver5.view.dialogs;

import ver5.SharedClasses.FontManager;
import ver5.SharedClasses.ui.MyLbl;
import ver5.view.dialogs.Navigation.BackOkInterface;
import ver5.view.dialogs.Navigation.BackOkPnl;
import ver5.view.dialogs.game_select.buttons.SelectablesList;

import javax.swing.*;
import java.awt.*;

public class WinPnl extends JPanel {
    private final static Insets insets = new Insets(5, 5, 5, 5);
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
        this(1, new Header(icon), centerHeader);
    }

    public WinPnl(int cols, Header header, boolean centerHeader) {
//        super(cols == 1 ? new GridLayout(0, 1) : new GridBagLayout());

        super(new GridBagLayout());
        this.cols = cols;
        topPnlSetup(centerHeader, header);
    }

    private void topPnlSetup(boolean centerHeader, Header header) {
        this.headerPnl = new JPanel() {{
            setLayout(new GridBagLayout());
        }};
        MyLbl lbl = new MyLbl(header.txt, header.icon, SwingConstants.CENTER) {{
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
        if (comp instanceof SelectablesList selectablesList && selectablesList.isList) {
            comp = new JScrollPane(selectablesList) {{
                setPreferredSize(new Dimension(350, 350));
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
        this(1, new Header(header), centerHeader);
    }

    public void addHeaderIcon(ImageIcon icon) {
//        topLblInsideTopPnl.setIcon(icon);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = topPnlRow++;

        headerPnl.add(new MyLbl(icon), gbc);

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
}
