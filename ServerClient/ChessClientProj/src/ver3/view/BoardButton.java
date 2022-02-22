package ver3.view;

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

    public void setSelected(boolean selected) {
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
//        g2.dispose();
    }

    public void toggleSelected() {
        isSelected = !isSelected;
    }

    public void resetIcon() {
        setIcon(null);
    }
}
