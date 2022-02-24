package ver13.view.Graph;

import ver13.SharedClasses.DBActions.Graphable.GraphElement;
import ver13.SharedClasses.DBActions.Graphable.GraphElementType;
import ver13.SharedClasses.DBActions.Graphable.Graphable;
import ver13.SharedClasses.FontManager;
import ver13.SharedClasses.ui.MyLbl;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Graph extends JPanel {

    public Graph(String header, GraphElement... graphElements) {
        setLayout(new GridBagLayout());
        assert graphElements.length != 0;

        ArrayList<UIGraphElement> uiGraphElements = new ArrayList<>();
        for (GraphElement graphElement : graphElements) {
            uiGraphElements.add(new UIGraphElement(graphElement));
        }

        int col = 0;
        int row = 0;
        int max = 0;
        int avg = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = row++;
        add(new MyLbl(header, FontManager.statistics), gbc);
        for (UIGraphElement graphElement : uiGraphElements) {
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = col++;
            gbc.gridy = row;
            gbc.anchor = GridBagConstraints.LINE_END;
            gbc.gridheight = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.VERTICAL;
            max = Math.max(max, graphElement.getNum());
            avg += graphElement.getNum();
            add(graphElement.getPnl(), gbc);
        }
        avg /= graphElements.length;
        for (UIGraphElement graphElement : uiGraphElements) {
            graphElement.createStats(avg);
        }
    }

    public static Graph createGraph(Graphable graphable) {
        return new Graph(graphable.header(), graphable.elements());
    }

    public static void main(String[] args) {
        new JFrame() {{
//            setIconImage(IconManager.statisticsIcon.getImage());
            int size = 5;
            UIGraphElement[] graphElements = new UIGraphElement[size];
            int[] options = {10, 20, 400, 200, 300};
            for (int i = 0; i < size; i++) {
                graphElements[i] = new UIGraphElement(options[new Random().nextInt(options.length)], "name", GraphElementType.values()[new Random().nextInt(GraphElementType.values().length)]);
            }
            add(new Graph("*username*", graphElements));
            pack();
            setSize(getPreferredSize());
            setVisible(true);
        }};
    }
}
