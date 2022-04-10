package ver14.view.Board;

import java.awt.*;

public class MyColor extends Color {
    public MyColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public MyColor(int r, int g, int b) {
        super(r, g, b);
    }

    public MyColor(String s) {
        this(Color.decode(s));
    }

    public MyColor(Color color) {
        super(color.getRGB());
    }

    public Color movedClr() {
        return Color.decode("#9fc0a2");
    }

}
