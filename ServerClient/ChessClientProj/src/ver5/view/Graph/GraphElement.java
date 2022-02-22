package ver5.view.Graph;

import ver5.SharedClasses.FontManager;
import ver5.SharedClasses.ui.MyLbl;
import ver5.view.IconManager;

import javax.swing.*;
import java.awt.*;

public class GraphElement extends JPanel {
    private final static int maxHeight = 200;
    private static final Font font = FontManager.statistics;
    public final int num;
    private final GraphElementType graphElementType;
    private final String name;
    private final JPanel innerPnl;

    public GraphElement(int num, String name, GraphElementType graphElementType) {
        this.num = num;
        this.name = name;
        this.graphElementType = graphElementType;

        setLayout(new BorderLayout());
        innerPnl = new JPanel();
        innerPnl.setLayout(new BoxLayout(innerPnl, BoxLayout.Y_AXIS));
        add(innerPnl, BorderLayout.PAGE_END);
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
        innerPnl.add(Box.createRigidArea(new IconManager.Size(2)));
        innerPnl.add(new MyLbl(name, font));

    }

}
