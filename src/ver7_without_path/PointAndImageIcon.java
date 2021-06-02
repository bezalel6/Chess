package ver7_without_path;

import javax.swing.*;

public class PointAndImageIcon {
    private int x, y;
    private ImageIcon icon;

    public PointAndImageIcon(int x, int y, ImageIcon icon) {
        this.x = x;
        this.y = y;
        this.icon = icon;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ImageIcon getIcon() {
        return icon;
    }


    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
}
