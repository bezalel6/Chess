package ver14.view.IconManager;

import java.awt.*;

/**
 * Size - some general improvements to {@link Dimension}.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Size extends Dimension {
    /**
     * Instantiates a new Size.
     */
    public Size() {
        this(80);
    }

    /**
     * Instantiates a new Size.
     *
     * @param size the size
     */
    public Size(int size) {
        this(size, size);
    }

    /**
     * Instantiates a new Size.
     *
     * @param width  the width
     * @param height the height
     */
    public Size(int width, int height) {
        super(width, height);
    }

    /**
     * Instantiates a new Size.
     *
     * @param size the size
     */
    public Size(Size size) {
        this(size.width, size.height);
    }

    /**
     * Instantiates a new Size.
     *
     * @param size the size
     */
    public Size(Dimension size) {
        this(size.width, size.height);
    }

    /**
     * Size size.
     *
     * @param _size the size
     * @return the size
     */
    public static Size size(Dimension... _size) {
        return _size.length == 0 ? new Size() : new Size(_size[0]);
    }

    /**
     * Size size.
     *
     * @param _size the size
     * @return the size
     */
    public static Size size(Size... _size) {
        return _size.length == 0 ? new Size() : _size[0];
    }

    /**
     * Min square size.
     *
     * @param size the size
     * @return the size
     */
    public static Size minSquare(Dimension size) {
        return new Size(Math.min(size.height, size.width));
    }

    /**
     * Max size.
     *
     * @param size the size
     * @return the size
     */
    public static Size max(Dimension size) {
        return new Size(Math.max(size.height, size.width));
    }

    /**
     * Add size.
     *
     * @param d   the d
     * @param add the add
     * @return the size
     */
    public static Size add(Dimension d, int add) {
        return new Size(d.width + add, d.height + add);
    }

    /**
     * Min size.
     *
     * @param size1 the size 1
     * @param size2 the size 2
     * @return the size
     */
    public static Size min(Dimension size1, Dimension size2) {
        return new Size(size1.width + size1.height > size2.height + size2.width ? size2 : size1);
    }

    /**
     * Max combo size.
     *
     * @param size1 the size 1
     * @param size2 the size 2
     * @return the size
     */
    public static Size maxCombo(Dimension size1, Dimension size2) {
        return new Size(Math.max(size1.width, size2.width), Math.max(size1.height, size2.height));
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Padding size.
     *
     * @param padding the padding
     * @return the size
     */
    public Size padding(int padding) {
        return padding(MyInsets.insets(padding));
    }

    /**
     * Padding size.
     *
     * @param insets the insets
     * @return the size
     */
    public Size padding(Insets insets) {
        return new Size(width - (insets.left + insets.right), height - (insets.top + insets.bottom));
    }

    /**
     * Add size.
     *
     * @param size the size
     * @return the size
     */
    public Size add(Dimension size) {
        return new Size(width + size.width, height + size.height);
    }

    /**
     * Create min combo dimension.
     *
     * @param other the other
     * @return the dimension
     */
    public Dimension createMinCombo(Dimension other) {
        return new Size(Math.min(width, other.width), Math.min(height, other.height));
    }

    /**
     * Min dimension.
     *
     * @param other the other
     * @return the dimension
     */
    public Dimension min(Dimension other) {
        return (width + height <= other.height + other.width) ? this : other;
    }

    /**
     * Mult size.
     *
     * @param mult the mult
     * @return the size
     */
    public Size mult(double mult) {
        return new Size(this) {{
            multMe(mult);
        }};
    }

    /**
     * Mult me.
     *
     * @param mult the mult
     */
    public void multMe(double mult) {
        width *= mult;
        height *= mult;
    }

    /**
     * Max diff int.
     *
     * @param size the size
     * @return the int
     */
    public int maxDiff(Dimension size) {
        return Math.max(Math.abs(width - size.width), Math.abs(height - size.height));
    }

    /**
     * Keep ratio.
     *
     * @param size the size
     */
    public void keepRatio(Size size) {
        if (size.isValid()) {
            width *= (double) size.width / size.height;
            height *= (double) size.height / size.width;
        }
    }

    /**
     * Is valid boolean.
     *
     * @return the boolean
     */
    public boolean isValid() {
        return width > 0 && height > 0;
    }

    /**
     * My insets.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class MyInsets {
        /**
         * Insets insets.
         *
         * @param a the a
         * @return the insets
         */
        public static Insets insets(int a) {
            return new Insets(a, a, a, a);
        }
    }

    /**
     * Ratio size.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class RatioSize extends Size {
        /**
         * Instantiates a new Ratio size.
         */
        public RatioSize() {
        }

        /**
         * Instantiates a new Ratio size.
         *
         * @param size the size
         */
        public RatioSize(int size) {
            super(size);
        }

        /**
         * Instantiates a new Ratio size.
         *
         * @param width  the width
         * @param height the height
         */
        public RatioSize(int width, int height) {
            super(width, height);
        }

        /**
         * Instantiates a new Ratio size.
         *
         * @param size the size
         */
        public RatioSize(Size size) {
            super(size);
        }

        /**
         * Instantiates a new Ratio size.
         *
         * @param size the size
         */
        public RatioSize(Dimension size) {
            super(size);
        }
    }
}
