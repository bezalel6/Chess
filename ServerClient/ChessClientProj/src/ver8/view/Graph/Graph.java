package ver8.view.Graph;

import ver8.SharedClasses.FontManager;
import ver8.SharedClasses.PlayerStatistics;
import ver8.SharedClasses.ui.MyLbl;
import ver8.view.IconManager;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Graph extends JPanel {

    public Graph(String header, GraphElement... graphElements) {
        setLayout(new GridBagLayout());
        assert graphElements.length != 0;
        int col = 0;
        int row = 0;
        int max = 0;
        int avg = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = row++;
        add(new MyLbl(header, FontManager.statistics), gbc);
        for (GraphElement graphElement : graphElements) {
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = col++;
            gbc.gridy = row;
            gbc.anchor = GridBagConstraints.LINE_END;
            gbc.gridheight = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.VERTICAL;
            max = Math.max(max, graphElement.num);
            avg += graphElement.num;
            add(graphElement, gbc);
        }
        avg /= graphElements.length;
        for (GraphElement graphElement : graphElements) {
            graphElement.createStats(avg);
        }
    }

    public static Graph createStatisticsGraph(PlayerStatistics playerStatistics) {
        GraphElement e1 = new GraphElement(playerStatistics.numOfWins, "Wins", GraphElementType.GREEN);
        GraphElement e2 = new GraphElement(playerStatistics.numOfLosses, "Losses", GraphElementType.RED);
        GraphElement e3 = new GraphElement(playerStatistics.numOfTies, "Ties", GraphElementType.YELLOW);
        return new Graph(playerStatistics.username, e1, e2, e3);
    }

    public static void main(String[] args) {
        new JFrame() {{
            setIconImage(IconManager.statisticsIcon.getImage());
            int size = 5;
            GraphElement[] graphElements = new GraphElement[size];
            int[] options = {10, 20, 400, 200, 300};
            for (int i = 0; i < size; i++) {
                graphElements[i] = new GraphElement(options[new Random().nextInt(options.length)], "name", GraphElementType.values()[new Random().nextInt(GraphElementType.values().length)]);
            }
            add(new Graph("*username*", graphElements));
            pack();
            setSize(getPreferredSize());
            setVisible(true);
        }};
    }
}
