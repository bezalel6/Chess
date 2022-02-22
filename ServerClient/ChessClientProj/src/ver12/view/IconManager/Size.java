package ver12.view.IconManager;

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

    public static Size min(Dimension size) {
        return new Size(Math.min(size.height, size.width));
    }

    public static Dimension max(Dimension size) {
        return new Size(Math.max(size.height, size.width));
    }

    public boolean isValid() {
        return width > 0 || height > 0;
    }

    public Size padding(Insets insets) {
        return new Size(width - (insets.left + insets.right), height - (insets.top + insets.bottom));
    }

    public void multBy(double mult) {
        width *= mult;
        height *= mult;
    }

    public int maxDiff(Dimension size) {
        return Math.max(Math.abs(width - size.width), Math.abs(height - size.height));
    }
}
