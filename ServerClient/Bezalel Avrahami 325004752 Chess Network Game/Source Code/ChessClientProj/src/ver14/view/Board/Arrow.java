package ver14.view.Board;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * represents an Arrow that can be painted on the screen.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
class Arrow {
    /**
     * The Start.
     */
    private final Point start, /**
     * The End.
     */
    end;
    /**
     * The Barb.
     */
    int barb = 50;                   // barb length WAS 50
    /**
     * The Phi.
     */
    double phi = Math.PI / 6;             // 30 degrees barb angle WAS 6
    /**
     * The Clr.
     */
    private Color clr;


    /**
     * Instantiates a new Arrow.
     *
     * @param start the start
     * @param end   the end
     * @param clr   the clr
     */
    public Arrow(Point start, Point end, Color clr) {
        this.start = start;
        this.end = end;
        this.clr = clr;
    }

    /**
     * Sets clr.
     *
     * @param clr the clr
     */
    public void setClr(Color clr) {
        this.clr = clr;
    }

    /**
     * Draws this arrow.
     *
     * @param g2 the g 2
     */
    public void draw(Graphics2D g2) {
        if (start == null || end == null || start.equals(end)) return;
        g2.setStroke(new BasicStroke(10));
        g2.setColor(clr);
        g2.draw(new Line2D.Double(start.x, start.y, end.x, end.y));
//        g2.draw(new Line2D.Double(start.x, start.y, end.x - 1, end.y - 1));
        double theta = Math.atan2(end.y - start.y, end.x - start.x);
        int x0 = end.x, y0 = end.y;
        double x = x0 - barb * Math.cos(theta + phi);
        double y = y0 - barb * Math.sin(theta + phi);

        g2.draw(new Line2D.Double(x0, y0, x, y));
        x = x0 - barb * Math.cos(theta - phi);
        y = y0 - barb * Math.sin(theta - phi);
        g2.draw(new Line2D.Double(x0, y0, x, y));
    }


    /**
     * Equals boolean.
     *
     * @param other the other
     * @return the boolean
     */
    public boolean equals(Arrow other) {
        return start.equals(other.start) && end.equals(other.end);
    }
}
