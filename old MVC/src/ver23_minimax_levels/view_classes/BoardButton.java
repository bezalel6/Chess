package ver23_minimax_levels.view_classes;

import ver23_minimax_levels.Location;

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

    public void resetButton() {
        isSelected = false;
        setBackground(startingBackgroundColor);
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public void setIcon(Icon defaultIcon) {
        super.setIcon(defaultIcon);
        super.setDisabledIcon(defaultIcon);
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