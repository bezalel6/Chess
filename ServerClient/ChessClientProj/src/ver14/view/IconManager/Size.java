package ver14.view.IconManager;

import java.awt.*;

public class Size extends Dimension {
    public Size() {
        this(80);
    }

    public Size(int size) {
        this(size, size);
    }

    public Size(int width, int height) {
        super(width, height);
    }

    public Size(Size size) {
        this(size.width, size.height);
    }

    public Size(Dimension size) {
        this(size.width, size.height);
    }

    public static Size size(Dimension... _size) {
        return _size.length == 0 ? new Size() : new Size(_size[0]);
    }

    public static Size size(Size... _size) {
        return _size.length == 0 ? new Size() : _size[0];
    }

    public static Size minSquare(Dimension size) {
        return new Size(Math.min(size.height, size.width));
    }

    public static Dimension max(Dimension size) {
        return new Size(Math.max(size.height, size.width));
    }

    public static Size add(Dimension d, int add) {
        return new Size(d.width + add, d.height + add);
    }

    public static Dimension min(Dimension size1, Size size2) {
        return size1.width + size1.height > size2.height + size2.width ? size1 : size2;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Size padding(Insets insets) {
        return new Size(width - (insets.left + insets.right), height - (insets.top + insets.bottom));
    }

    public Dimension createMinCombo(Dimension other) {
        return new Size(Math.min(width, other.width), Math.min(height, other.height));
    }

    public Dimension min(Dimension other) {
        return (width + height <= other.height + other.width) ? this : other;
    }

    public Size mult(double mult) {
        return new Size(this) {{
            multMe(mult);
        }};
    }

    public void multMe(double mult) {
        width *= mult;
        height *= mult;
    }

    public int maxDiff(Dimension size) {
        return Math.max(Math.abs(width - size.width), Math.abs(height - size.height));
    }

    public void keepRatio(Size size) {
        if (size.isValid()) {
            width *= (double) size.width / size.height;
            height *= (double) size.height / size.width;
        }
    }

    public boolean isValid() {
        return width > 0 && height > 0;
    }

    public static class RatioSize extends Size {
        public RatioSize() {
        }

        public RatioSize(int size) {
            super(size);
        }

        public RatioSize(int width, int height) {
            super(width, height);
        }

        public RatioSize(Size size) {
            super(size);
        }

        public RatioSize(Dimension size) {
            super(size);
        }
    }
}
