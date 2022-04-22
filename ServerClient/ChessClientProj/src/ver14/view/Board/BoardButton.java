package ver14.view.Board;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.SharedClasses.UI.FontManager;
import ver14.view.IconManager.IconManager;
import ver14.view.IconManager.Size;
import ver14.view.Shapes.ShapesHelper;
import ver14.view.View;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.util.ArrayList;

public class BoardButton extends MyJButton {
    private static final double iconMultiplier = .8;
    private final static Color checkColor = new Color(186, 11, 11, 255);
    private final static Color captureColor = new Color(0, 0, 0, 255 / 4);
    private final static Color promotingColor = new Color(151, 109, 3);
    private static int ICON_SIZE = 50;
    private final MyColor startingBackgroundColor;
    private final ArrayList<State> btnStates;
    private final View view;
    private final ViewLocation btnLoc;
    private boolean isSelected = false;
    private Icon ogQualityIcon;
    private Piece piece = null;
    private boolean wasUnlocked = false;
    private Color beforeLockBg;
    private Color selectedClr;
    private Icon hiddenIcon = null;

    public BoardButton(ViewLocation btnLoc, MyColor startingBackgroundColor, View view) {
        this.startingBackgroundColor = startingBackgroundColor;
        this.btnLoc = btnLoc;
        this.view = view;
        this.btnStates = new ArrayList<>();
        this.beforeLockBg = startingBackgroundColor;
        setFont(FontManager.boardButtons);

        setActionCommand("");
        setUI(new BasicButtonUI());
    }

    public void endHover() {
        removeState(State.HOVERED);
    }

    private void removeState(State removing) {
        btnStates.remove(removing);
        repaint();
    }

    public void hideIcon() {
        hiddenIcon = getIcon();
        setIcon(null);
    }

    @Override
    public String toString() {
        return "BoardButton{" +
                "btnLoc=" + btnLoc +
                ", piece=" + piece +
                '}';
    }

    public Icon getHiddenIcon() {
        return hiddenIcon;
    }

    public void unHideIcon() {
        setIcon(hiddenIcon);
        hiddenIcon = null;
    }

    public void setStates(ArrayList<State> states) {
        resetBackground();
        btnStates.addAll(states);
        repaint();
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
        repaint();
    }

    public ArrayList<State> getBtnStates() {
        return btnStates;
    }

    public void setAsCurrent() {
        addState(State.CURRENT);
    }

    private void addState(State adding) {
        btnStates.add(adding);
        repaint();
    }

    public void setAsCheck() {
        addState(State.CHECK);
    }

    public void setAsPromotion() {
        addState(State.PROMOTING);
    }

    public void setAsMovable() {
        addState(State.CAN_MOVE_TO);
    }

    public void setAsCapture() {
        addState(State.CAPTURE);
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        synchronized (view.boardLock) {
            this.piece = piece;
            setOgQualityIcon(IconManager.getPieceIcon(piece));
        }
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
    public void doClick() {
        System.out.println("ffff");
//        super.doClick();
    }

    @Override
    public void doClick(int pressTime) {
        System.out.println("ffff");

//        super.doClick(pressTime);
    }

    @Override
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        super.setDisabledIcon(icon);
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

    public void startHover() {
        addState(State.HOVERED);
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

        super.paintComponent(g);
        for (State state : btnStates) {
            switch (state) {
                case CHECK -> {
                    setBackground(checkColor);
                }
                case CAPTURE -> {
                    ShapesHelper.paintTrianglesBorder(g, captureColor, getWidth() / 4, this);
                }
                case CAN_MOVE_TO -> {
                    ShapesHelper.paintCircle(g2, Color.decode("#9fc0a2"), this);
                }
                case HOVERED -> {
                    if (isEnabled()) {
                        Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                        setCursor(cursor);
//                        ShapesHelper.paintTrianglesBorder(g, hoverClr, getWidth() / 3, this);
                    }
                }
                case CURRENT -> {
                    if (!isEnabled())
                        setEnabled(true);
                }
                case PROMOTING -> {
                    setBackground(promotingColor);
                }
                case MOVING_FROM, MOVING_TO -> {
                    setBackground(startingBackgroundColor.movedClr());
                }
            }
        }
        if (isSelected) {
            g2.setStroke(new BasicStroke(5));
            int nGap = 7;
            int nXPosition = nGap;
            int nYPosition = nGap;
            int nHeight = getHeight() - nGap * 2;
            int nWidth = getWidth() - nGap * 2;

            g2.setColor(selectedClr);
//            g2.setColor(new Color(0, 0, 0, .5f));
            g2.drawOval(nXPosition, nYPosition, nWidth, nHeight);
            ((Graphics2D) g).setStroke(new BasicStroke());
        }

//        g2.dispose();
    }

    public void toggleSelected() {
        isSelected = !isSelected;
        if (isSelected)
            captureSelectedClr();
    }

    private void captureSelectedClr() {
        selectedClr = view.getBoardPnl().getBoardOverlay().currentColor();
    }

    public void clickMe() {
        System.out.println("clicking " + piece);
        view.boardButtonPressed(btnLoc);
//        System.out.println(Thread.currentThread().getStackTrace()[2] + "clicked me");
//        super.doClick();
    }

    public boolean canMoveTo() {
        return btnStates.contains(State.CAN_MOVE_TO) || btnStates.contains(State.CAPTURE);
    }

    public void reset() {
        setIcon(null);
        resetBackground();
        hiddenIcon = null;
        piece = null;
    }

    public void movingFrom() {
        addState(State.MOVING_FROM);
    }

    public void movingTo() {
        addState(State.MOVING_TO);
    }


    public enum State {
        CHECK, CAPTURE, CAN_MOVE_TO, CURRENT, PROMOTING, MOVING_FROM, MOVING_TO, HOVERED;
    }
}
