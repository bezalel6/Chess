package ver14.view.Graph;

import ver14.SharedClasses.DBActions.DBResponse.Graphable.GraphElement;
import ver14.SharedClasses.DBActions.DBResponse.Graphable.GraphElementType;
import ver14.SharedClasses.DBActions.DBResponse.Graphable.Graphable;
import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.UI.MyLbl;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Graph extends JPanel {

    private Axis axis;

    public Graph(String header, GraphElement... graphElements) {
        setLayout(new BorderLayout());
//        setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        assert graphElements.length != 0;

        double max = Arrays.stream(graphElements).max(Comparator.comparingDouble(GraphElement::getNum)).orElseThrow().getNum();
        double avg = 0;

        Header h = new Header(header);
        h.setFont(FontManager.statistics);
        h.getLbl().underline();

        add(h, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 0));

        JPanel names = new JPanel(new GridLayout(1, 0));
        names.add(new MyLbl(""));

        axis = new Axis();
        center.add(axis);

        add(center, BorderLayout.CENTER);
        add(names, BorderLayout.SOUTH);

        final int minSize = 10;
        final int lineWidth = 20;

        for (GraphElement element : graphElements) {
            center.add(new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int fullHeight = (int) (((element.getNum()) / max) * this.getHeight());
                    if (element.getNum() != 0)
                        fullHeight = Math.max(fullHeight, minSize);
                    g.setColor(Color.BLACK);
                    g.fillRect(0, getHeight() - fullHeight, lineWidth, fullHeight);
                }
            });
            names.add(new MyLbl(element.toString(), FontManager.statistics));
        }

        Arrays.stream(names.getComponents()).forEach(component -> component.setSize(component.getPreferredSize()));
        axis.init(max);

        setPreferredSize(new Size(500));
    }


    public static Graph createGraph(Graphable graphable) {
        return new Graph(graphable.header(), graphable.elements());
    }

    public static void main(String[] args) {
        new JFrame() {{
//            setIconImage(IconManager.statisticsIcon.getImage());
            int size = 5;
            GraphElement[] graphElements = new GraphElement[size];
            int[] options = {10, 20, 400, 200, 300};
            for (int i = 0; i < size; i++) {
                graphElements[i] = new GraphElement(options[new Random().nextInt(options.length)], "name", GraphElementType.values()[new Random().nextInt(GraphElementType.values().length)]);
            }
            add(new Graph("*username*", graphElements));
            pack();
//            setSize(getPreferredSize());
            setSize(500, 500);
            setVisible(true);
        }};
    }
}
