package ver14.view.Board;

import ver14.SharedClasses.pieces.Piece;
import ver14.SharedClasses.ui.MyJButton;
import ver14.view.IconManager.IconManager;
import ver14.view.IconManager.Size;
import ver14.view.View;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class BoardButton extends MyJButton {
    private static final double iconMultiplier = .8;
    private static int ICON_SIZE = 50;
    private final Color checkColor = new Color(186, 11, 11, 255);
    private final Color currentColor = new Color(0, 0, 255, 255);
    private final Color captureColor = Color.red;
    private final Color canMoveToClr = Color.yellow;
    private final Color promotingColor = new Color(151, 109, 3);
    private final Color startingBackgroundColor;
    private final ArrayList<State> btnStates;
    private final View view;
    private ViewLocation btnLoc;
    private boolean isSelected = false;
    private Icon ogQualityIcon;
    private Piece piece = null;
    private boolean wasUnlocked = false;
    private Color beforeLockBg;
    private Color beforeHoverClr;
    private boolean isHovering = false;

    public BoardButton(ViewLocation btnLoc, Color startingBackgroundColor, View view) {
        this.startingBackgroundColor = startingBackgroundColor;
        this.btnLoc = btnLoc;
        this.view = view;
        this.btnStates = new ArrayList<>();
        this.beforeLockBg = startingBackgroundColor;

        setActionCommand("");
        setUI(new BasicButtonUI());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                startHover();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                endHover();
            }
        });
    }

    private void startHover() {
        this.isHovering = true;
        if (isEnabled()) {
            beforeHoverClr = getBackground();
            super.setBackground(Color.BLUE);
        }
    }

    private void endHover() {
        this.isHovering = false;
        setBackground(beforeHoverClr);
    }

    public void setStates(ArrayList<State> states) {
        resetBackground();
        btnStates.addAll(states);
        updateState();
    }

    public ArrayList<State> getBtnStates() {
        return btnStates;
    }

    private void removeState(State removing) {
        btnStates.remove(removing);
        updateState();
    }

    public void updateState() {

        for (State state : btnStates) {
            switch (state) {
                case CHECK -> {
                    setBackground(checkColor);
                }
                case CAPTURE -> {
                    setBackground(captureColor);
                }
                case CAN_MOVE_TO -> {
                    setBackground(canMoveToClr);
                }
                case CURRENT -> {
                }
                case PROMOTING -> {
                    setBackground(promotingColor);
                }
            }
        }
    }

    public Color getBeforeHoverClr() {
        return beforeHoverClr;
    }

    public void setAsCurrent() {
        addState(State.CURRENT);
    }

    private void addState(State adding) {
        btnStates.add(adding);
        updateState();
    }

    public void setAsCheck() {
        addState(State.CHECK);
    }

    public void setAsPromotion() {
        addState(State.PROMOTING);
    }

    public void setAsMovable() {
        setBackground(canMoveToClr);
    }

    public void setAsCapture() {
        addState(State.CAPTURE);
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        setOgQualityIcon(IconManager.getPieceIcon(piece));
    }

    public void setPiece(BoardButton prevBtn) {
        setPiece(prevBtn.piece);
    }

    public ViewLocation getBtnLoc() {
        return btnLoc;
    }


    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        super.setDisabledIcon(icon);
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        checkHoverStatus();
    }

    public void setLocked(boolean lock) {
        if (lock) {
            beforeLockBg = getBackground();
            wasUnlocked = isEnabled();
            setEnabled(false);
        } else {
            setBackground(beforeLockBg);
            setEnabled(wasUnlocked);
        }
    }

    private void checkHoverStatus() {
        if (isHovering)
            startHover();
    }

    private int getIconHeight() {
        if (getHeight() > getWidth())
            return getIconWidth();
        if (getHeight() != 0) {
            ICON_SIZE = (int) (getHeight() * iconMultiplier);
        }
        return ICON_SIZE;
    }

    private int getIconWidth() {
        if (getWidth() > getHeight())
            return getIconHeight();
        if (getWidth() != 0) {
            ICON_SIZE = (int) (getWidth() * iconMultiplier);
        }
        return ICON_SIZE;
    }

    private void setOgQualityIcon(Icon ogQualityIcon) {
        this.ogQualityIcon = ogQualityIcon;
        setIcon(ogQualityIcon);
        scaleIcon();
    }


    public synchronized void scaleIcon() {
        if (getIcon() == null || ogQualityIcon == null) return;
        ImageIcon newIcon = IconManager.copyImage(ogQualityIcon);
        newIcon = IconManager.scaleImage(newIcon, new Size(getIconWidth(), getIconHeight()));
        super.setIcon(newIcon);
        super.setDisabledIcon(newIcon);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
//        g2.setColor(getBackground());
//        g2.setStroke(new BasicStroke(1));
//        g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
        super.paintComponent(g);
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

    @Override
    public void setBackground(Color bg) {
        if (getBackground() != bg) {
            beforeHoverClr = bg;
            super.setBackground(bg);
        }
    }

    public void toggleSelected() {
        isSelected = !isSelected;
    }

    public void reset() {
        setIcon(null);
        resetBackground();
        piece = null;
    }

    public void resetBackground() {
        isSelected = false;
        resetStates();
        setBackground(startingBackgroundColor);
        if (view != null)
            view.repaint();
    }

    private void resetStates() {
        btnStates.clear();
        updateState();
    }


    public enum State {
        CHECK, CAPTURE, CAN_MOVE_TO, CURRENT, PROMOTING;
    }
}