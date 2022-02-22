package ver10.view.Graph;

import ver10.SharedClasses.DBActions.Graphable.GraphElement;
import ver10.SharedClasses.DBActions.Graphable.GraphElementType;
import ver10.SharedClasses.FontManager;
import ver10.SharedClasses.ui.MyLbl;
import ver10.view.IconManager.IconManager;
import ver10.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;

public class UIGraphElement extends GraphElement {
    private final static int maxHeight = 200;
    private static final Font font = FontManager.statistics;
    private final JPanel pnl;
    private final JPanel innerPnl;

    public UIGraphElement(GraphElement element) {
        this(element.getNum(), element.getName(), element.getGraphElementType());
    }

    public UIGraphElement(int num, String name, GraphElementType graphElementType) {
        super(num, name, graphElementType);
        pnl = new JPanel();
        pnl.setLayout(new BorderLayout());
        innerPnl = new JPanel();
        innerPnl.setLayout(new BoxLayout(innerPnl, BoxLayout.Y_AXIS));
        pnl.add(innerPnl, BorderLayout.PAGE_END);
    }

    public JPanel getPnl() {
        return pnl;
    }

    public void createStats(int avgValue) {
        innerPnl.add(new MyLbl(num + "", font));

        if (num > 0) {
            ImageIcon icon = IconManager.getStatisticsIcon(graphElementType);
            int numOfIcons = (int) (((double) num / avgValue) * 100);
            numOfIcons = Math.max(numOfIcons, 1);
            if (icon.getIconHeight() * numOfIcons > maxHeight) {
                numOfIcons = maxHeight / icon.getIconHeight();
                innerPnl.add(new MyLbl("....", font));
            }
            for (int i = 0; i < numOfIcons; i++) {
                innerPnl.add(new MyLbl(icon, font));
            }
        }
        innerPnl.add(Box.createRigidArea(new Size(2)));
        innerPnl.add(new MyLbl(name, font));

    }

}
