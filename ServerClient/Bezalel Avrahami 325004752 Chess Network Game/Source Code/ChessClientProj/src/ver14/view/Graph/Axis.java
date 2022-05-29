package ver14.view.Graph;

import ver14.SharedClasses.UI.FontManager;

import javax.swing.*;
import java.awt.*;

public class Axis extends JComponent {

    private static final int WIDTH = 10;
    private static final int LEVEL_HEIGHT = 5;
    int spaceOffLeft = 50;
    private Double max = null;

    public void init(double max) {
        this.max = max;
//        this.max = 11.0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.fillRect(spaceOffLeft, 0, WIDTH, getHeight());

        if (max == null)
            return;

        paintLevel(max, g);
        for (int i = 1; i <= 10; i++) {
            paintLevel((max / 10) * i, g);
        }

    }

    private void paintLevel(double level, Graphics g) {
        g.setColor(Color.BLACK);

        int levelY = (int) (((max - level) / max) * (getHeight()));

        String str = level + "";
        Font font = FontManager.statistics;
        g.setFont(font);
        int y = levelY + (font.getSize() / 2);
        y = Math.max(y, font.getSize());
        g.drawString(str, 2, y);
        int lineWidth = 20;
        g.fillRect(spaceOffLeft - lineWidth / 3, levelY, lineWidth, LEVEL_HEIGHT);
    }
}
