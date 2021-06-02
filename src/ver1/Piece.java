package ver1;

import javax.swing.*;
import java.awt.*;

public class Piece {
    private String  color;
    private String type;
    private ImageIcon image;
    private int iconSize = 100,firstRow;
    private Location loc;
    private JButton btn;
    private boolean isEaten = false;

    public Piece(String color, String type, Location loc,JButton btn) {
        this.color = color;
        this.type = type;
        this.loc = loc;
        this.btn=btn;

        setFirstRow();
        setImage();
    }
    private void setFirstRow()
    {
        if(color.equals("Black")) {
            if (type.equals("Pawn"))
                firstRow=1;
            else firstRow = 0;
        }
        else {
            if (type.equals("Pawn"))
                firstRow=6;
            else firstRow=7;
        }
    }

    public int getFirstRow() {
        return firstRow;
    }

    public boolean getEaten() {
        return isEaten;
    }

    public void setEaten() {
        isEaten = true;
    }

    public JButton getBtn() {
        return btn;
    }

    public void setBtn(JButton btn) {
        this.btn = btn;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public Location getLoc() {
        return loc;
    }

    private void setImage()
    {
        // טעינת התמונה מתוך הקובץ שנימצא בתקיית הנכסים
        image = new ImageIcon(Controller.class.getResource("/Assets/"+color+"/"+type+".png"));
        image = new ImageIcon(image.getImage().getScaledInstance((int)(iconSize), (int)(iconSize), Image.SCALE_SMOOTH));
    }
    public ImageIcon getImage()
    {
        return image;
    }
    public String getColor() {
        return color;
    }
    public String getType() {
        return type;
    }

    public int getIconSize() {
        return iconSize;
    }

    public boolean isWhite()
    {
        if(type.equals("White"))return true;
        return false;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
        setImage();
    }

    @Override
    public String toString() {
        return "Piece{" +
                "color='" + color + '\'' +
                ", type='" + type + '\'' +
                ", image=" + image +
                ", iconSize=" + iconSize +
                ", loc=" + loc +
                '}';
    }
}
