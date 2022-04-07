package ver14.view.Graph;

import ver14.SharedClasses.DBActions.DBResponse.Graphable.GraphElement;
import ver14.SharedClasses.DBActions.DBResponse.Graphable.GraphElementType;
import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.ui.MyLbl;
import ver14.view.IconManager.IconManager;
import ver14.view.IconManager.Size;

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

    public UIGraphElement(double num, String name, GraphElementType graphElementType) {
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

    public void createStats(double avgValue) {
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
