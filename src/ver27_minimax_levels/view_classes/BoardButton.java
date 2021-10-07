package ver27_minimax_levels.view_classes;

import ver27_minimax_levels.Location;

import javax.swing.*;
import java.awt.*;

public class BoardButton extends MyJButton {
    private Location btnLoc;
    private Color startingBackgroundColor;
    private ImageIcon unscaledIcon;
    private boolean isSelected = false;

    public BoardButton(BoardButton other) {
        copyConstructor(other);
    }

    public BoardButton(Location btnLoc, Color startingBackgroundColor) {
        this.startingBackgroundColor = startingBackgroundColor;
        this.btnLoc = btnLoc;
    }

    public void copyConstructor(BoardButton other) {
        this.btnLoc = new Location(other.btnLoc);
//        this.startingBackgroundColor = other.startingBackgroundColor;
        if (other.unscaledIcon != null)
            this.unscaledIcon = IconManager.copyImage(other.unscaledIcon);
        this.isSelected = other.isSelected;
        this.setIcon(other.unscaledIcon);
        super.setBounds(other.getBounds());
        super.setLocation(other.getLocation());
        super.setEnabled(other.isEnabled());
    }

    public Location getBtnLoc() {
        return btnLoc;
    }

    public void setBtnLoc(Location btnLoc) {
        this.btnLoc = btnLoc;
    }

    public void resetButton() {
        isSelected = false;
        setBackground(startingBackgroundColor);
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }

    public ImageIcon getUnscaledIcon() {
        return unscaledIcon;
    }

    public void setIcon(Icon defaultIcon, boolean scaleUp) {
        if (unscaledIcon == null || defaultIcon.getIconHeight() > unscaledIcon.getIconHeight())
            unscaledIcon = IconManager.copyImage((ImageIcon) defaultIcon);
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
        if (unscaledIcon == null || defaultIcon.getIconHeight() > unscaledIcon.getIconHeight())
            unscaledIcon = IconManager.copyImage((ImageIcon) defaultIcon);
        ImageIcon newIcon = IconManager.copyImage(defaultIcon);
        newIcon = IconManager.scaleImage(newIcon, getIconSize(), getIconSize());
        super.setIcon(newIcon);
        super.setDisabledIcon(newIcon);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isSelected) {
            Graphics2D g2 = (Graphics2D) g;
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
    }

    public void toggleSelected() {
        isSelected = !isSelected;
    }

}
