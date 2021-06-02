package ver9_Dialogs;

import javax.swing.*;
import java.awt.*;

public class PointAndImageIcon {
    private Point point;
    private ImageIcon icon;

    public PointAndImageIcon(Point point, ImageIcon icon) {
        this.point = point;
        this.icon = icon;
    }

    public Point getPoint() {
        return point;
    }

    public ImageIcon getIcon() {
        return icon;
    }


    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
}
