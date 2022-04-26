package ver14.view.Shapes;

import javax.swing.*;
import java.awt.*;

/**
 * The utility class Shapes helper.
 * can paint some shapes
 */
public class ShapesHelper {


    /**
     * Paint triangles border.
     *
     * @param g         the g
     * @param color     the color
     * @param size      the size
     * @param component the component
     */
    public static void paintTrianglesBorder(Graphics g, Color color, int size, JComponent component) {
        g.setColor(color);

        Polygon polygon = new Polygon(new int[]{0, 0, size}, new int[]{0, size, 0}, 3);
        g.fillPolygon(polygon);

        polygon = new Polygon(new int[]{0, 0, size}, new int[]{component.getHeight(), component.getHeight() - size, component.getHeight()}, 3);
        g.fillPolygon(polygon);

        polygon = new Polygon(new int[]{component.getWidth(), component.getWidth(), component.getWidth() - size}, new int[]{0, size, 0}, 3);
        g.fillPolygon(polygon);

        polygon = new Polygon(new int[]{component.getWidth(), component.getWidth(), component.getWidth() - size}, new int[]{component.getHeight(), component.getHeight() - size, component.getHeight()}, 3);
        g.fillPolygon(polygon);
    }

    public static void paintCircle(Graphics2D g2, Color color, JComponent component) {
        paintCircle(g2, color, 3, component);
    }

    /**
     * Paint circle.
     *
     * @param g2            the g 2
     * @param color         the color
     * @param diameterRatio the diameter ratio: smaller is larger
     * @param component     the component
     */
    public static void paintCircle(Graphics2D g2, Color color, int diameterRatio, JComponent component) {
        g2.setColor(color);
        int circleDiameter = component.getWidth() / diameterRatio;

        int x = (component.getWidth() / 2) - (circleDiameter / 2);
        int y = (component.getHeight() / 2) - (circleDiameter / 2);

        g2.fillOval(x, y, circleDiameter, circleDiameter);
    }


}
