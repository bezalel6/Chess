package ver31_fast_minimax.view_classes;

import ver31_fast_minimax.Location;

import javax.swing.*;
import java.awt.*;

public class BoardButton extends MyJButton {
    private Location btnLoc;
    private Color startingBackgroundColor;
    private boolean isSelected = false;


    public BoardButton(Location btnLoc, Color startingBackgroundColor) {
        this.startingBackgroundColor = startingBackgroundColor;
        this.btnLoc = btnLoc;
    }

    public Location getBtnLoc() {
        return btnLoc;
    }

    public void setBtnLoc(Location btnLoc) {
        this.btnLoc = btnLoc;
    }

    public void resetBackground() {
        isSelected = false;
        setBackground(startingBackgroundColor);
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }


    public void setIcon(Icon defaultIcon, boolean scaleUp) {
        if (scaleUp)
            setIcon(defaultIcon);
        else {
            if (defaultIcon != null && defaultIcon.getIconHeight() > getIconSize()) {
                setIcon(defaultIcon);
            } else {
                super.setIcon(defaultIcon);
                super.setDisabledIcon(defaultIcon);
            }
        }
    }

    private int getIconSize() {
        return (int) (getHeight() * 0.8);
    }

    @Override
    public void setIcon(Icon defaultIcon) {
        if (defaultIcon == null) {
            super.setIcon(null);
            super.setDisabledIcon(null);
            return;
        }
        ImageIcon newIcon = IconManager.copyImage(defaultIcon);
        int size = getIconSize();
        size = size == 0 ? 50 : size;
        newIcon = IconManager.scaleImage(newIcon, size, size);
        super.setIcon(newIcon);
        super.setDisabledIcon(newIcon);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (isSelected) {
            g2.setStroke(new BasicStroke(5));
            int nGap = 7;
            int nXPosition = nGap;
            int nYPosition = nGap;
            int nHeight = getHeight() - nGap * 2;
            int nWidth = getWidth() - nGap * 2;

            g2.setColor(new Color(0, 0, 0, .5f));
            g2.drawOval(nXPosition, nYPosition, nWidth, nHeight);
            ((Graphics2D) g).setStroke(new BasicStroke());
        }
//
//        if (myIcon != null) {
////            int width = getWidth();
////            int height = getHeight();
//            int flipMult = !view.isBoardFlipped() ? -1 : 1;
//
//            int width = myIcon.getIconWidth();
//            int height = myIcon.getIconHeight();
//            int bW = getWidth();
//            int bH = getHeight();
//
//            int div = 6;
//            int x = bW / div;
//            int y = height + bH / div;
//            x *= flipMult;
//            y *= flipMult;
////            int x = getX();
////            int y = getY();
//            g2.drawImage(myIcon.getImage(), x, y, width, flipMult * height, this);
//        }
        g2.dispose();
    }

    public void toggleSelected() {
        isSelected = !isSelected;
    }

    public void resetIcon() {
        setIcon(null);
    }
}
