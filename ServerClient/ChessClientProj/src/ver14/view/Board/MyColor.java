package ver14.view.Board;

import java.awt.*;

/**
 * My color - represents a color that has a 'moved' property. used to highlight squares after a piece moved onto or from them .
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MyColor extends Color {
    /**
     * Instantiates a new My color.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     * @param a the a
     */
    public MyColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    /**
     * Instantiates a new My color.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     */
    public MyColor(int r, int g, int b) {
        super(r, g, b);
    }

    /**
     * Instantiates a new My color.
     *
     * @param s the s
     */
    public MyColor(String s) {
        this(Color.decode(s));
    }

    /**
     * Instantiates a new My color.
     *
     * @param color the color
     */
    public MyColor(Color color) {
        super(color.getRGB());
    }

    /**
     * Moved clr color.
     *
     * @return the color
     */
    public Color movedClr() {
        return Color.decode("#9fc0a2");
    }

}
