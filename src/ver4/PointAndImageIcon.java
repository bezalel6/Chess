package ver4;

import javax.swing.*;
import java.awt.*;

public class PointAndImageIcon {
    private int x,y;
    private ImageIcon icon;

    public PointAndImageIcon(int x, int y, ImageIcon icon) {
        this.x = x;
        this.y = y;
        this.icon = icon;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ImageIcon getIcon() {
        return icon;
    }



    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
}
