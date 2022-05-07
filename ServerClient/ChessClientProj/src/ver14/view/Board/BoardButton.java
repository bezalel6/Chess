package ver14.view.Board;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Misc.EfficientGen;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.Utils.MathUtils;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.IconManager.IconManager;
import ver14.view.IconManager.Size;
import ver14.view.Shapes.ShapesHelper;
import ver14.view.View;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ver14.view.Board.State.*;

/**
 * represents a Board button.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class BoardButton extends MyJButton {
    /**
     * The constant ICON_MULTIPLIER.
     */
    private static final double ICON_MULTIPLIER = .8;
    /**
     * The constant CHECK_COLOR.
     */
    private final static Color CHECK_COLOR = new Color(186, 11, 11, 255);
    /**
     * The constant CAPTURE_COLOR.
     */
    private final static Color CAPTURE_COLOR = new Color(0, 0, 0, 255 / 4);
    /**
     * The constant PROMOTING_COLOR.
     */
    private final static Color PROMOTING_COLOR = new Color(151, 109, 3);
    /**
     * The constant HOVERED_CURSOR.
     */
    private static final Cursor HOVERED_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    /**
     * The constant iconSize.
     */
    private static int iconSize = 50;
    /**
     * The Starting background color.
     */
    private final MyColor startingBackgroundColor;
    /**
     * The View.
     */
    private final View view;
    /**
     * The Btn loc.
     */
    private final ViewLocation btnLoc;
    /**
     * The States callbacks.
     */
    private final Map<Integer, Callback<Graphics>> statesCallbacks = new HashMap<>();
    /**
     * The Single gen eff.
     */
    private final EfficientGen<Integer, ArrayList<Callback<Graphics>>> singleGenEff;
    /**
     * The Btn state.
     */
    private int btnState;
    /**
     * The Og quality icon.
     */
    private Icon ogQualityIcon;
    /**
     * The Piece.
     */
    private Piece piece = null;
    /**
     * The Was unlocked.
     */
    private boolean wasUnlocked = false;
    /**
     * The Before lock bg.
     */
    private Color beforeLockBg;
    /**
     * The Selected clr.
     */
    private Color selectedClr;
    /**
     * The Hidden icon.
     */
    private Icon hiddenIcon = null;

    /**
     * Instantiates a new Board button.
     *
     * @param btnLoc                  the btn loc
     * @param startingBackgroundColor the starting background color(white or black)
     * @param view                    the view
     */
    public BoardButton(ViewLocation btnLoc, MyColor startingBackgroundColor, View view) {
        this.startingBackgroundColor = startingBackgroundColor;
        this.btnLoc = btnLoc;
        this.view = view;
        this.btnState = 0;
        this.beforeLockBg = startingBackgroundColor;
        setFont(FontManager.boardButtons);

        setActionCommand("");
        setUI(new BasicButtonUI());

        setCallback(CHECK, g -> setBackground(CHECK_COLOR));
        setCallback(CAPTURE, g -> ShapesHelper.paintTrianglesBorder(g, CAPTURE_COLOR, getWidth() / 4, this));
        setCallback(CAN_MOVE_TO, g -> ShapesHelper.paintCircle(g, Color.decode("#9fc0a2"), this));
        setCallback(HOVERED, g -> {
            if (isEnabled()) {
                setCursor(HOVERED_CURSOR);
            }
        });
        setCallback(CURRENT, g -> {
            if (!isEnabled())
                setEnabled(true);
        });
        setCallback(DRAGGING, g -> {
            if (getIcon() != null) {
                hideIcon();
            }
        });
        setCallback(PROMOTING, g -> {
            setBackground(PROMOTING_COLOR);
        });
        setCallback(MOVING_FROM, g -> {
            setBackground(startingBackgroundColor.movedClr());
        });
        setCallback(MOVING_TO, g -> {
            setBackground(startingBackgroundColor.movedClr());
        });
        setCallback(CLICKED_ONCE, g -> {
        });
        setCallback(SELECTED, g -> {
            Graphics2D g2 = (Graphics2D) g;
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
        });

        singleGenEff = new EfficientGen<>(() -> {
            ArrayList<Callback<Graphics>> list = new ArrayList<>();
            if (!is(State.DRAGGING) && hiddenIcon != null)
                unHideIcon();

            int t = btnState;
            int i = 0;
//            tooptimize with n&(n-1)
            while (t != 0) {
                if ((t & 1) != 0)
                    list.add(statesCallbacks.get(i));
                t >>= 1;
                i++;
            }
            return list;
        });
    }

    /**
     * Sets callback.
     *
     * @param state    the state
     * @param callback the callback
     */
    private void setCallback(@State int state, Callback<Graphics> callback) {
        statesCallbacks.put(bitIndex(state), callback);
    }

    /**
     * Hide icon.
     */
    public void hideIcon() {
        hiddenIcon = getIcon();
        setIcon(null);
    }

    /**
     * checks if this button's state is on.
     *
     * @param state the state
     * @return true if this state is on, false otherwise
     */
    public boolean is(@State int state) {
        return (btnState & state) != 0;
    }

    /**
     * Un hide icon.
     */
    public void unHideIcon() {
        setIcon(hiddenIcon);
        hiddenIcon = null;
    }

    /**
     * Bit index int.
     *
     * @param powOf2 the pow of 2
     * @return the int
     */
    public static int bitIndex(int powOf2) {
        return (int) MathUtils.log(powOf2, 2);
    }

    /**
     * End hover.
     */
    public void endHover() {
        removeState(State.HOVERED);
    }

    /**
     * Remove state.
     *
     * @param removing the removing
     */
    public void removeState(int removing) {
        btnState &= ~(removing);
        repaint();
    }

    /**
     * Sets states.
     *
     * @param states the states
     */
    public void setStates(int states) {
        resetBackground();
        btnState = (states);
        repaint();
    }

    /**
     * Reset background.
     */
    public void resetBackground() {
        resetStates();
        setBackground(startingBackgroundColor);
        if (view != null)
            view.repaint();
    }

    /**
     * Reset states.
     */
    private void resetStates() {
        btnState = 0;
        repaint();
    }

    /**
     * Gets btn state.
     *
     * @return the btn state
     */
    public int getBtnState() {
        return btnState;
    }

    /**
     * Sets as current.
     */
    public void setAsCurrent() {
        addState(State.CURRENT);
    }

    /**
     * Add state.
     *
     * @param adding the adding
     */
    public void addState(int adding) {
        btnState |= (adding);
        repaint();
    }

    /**
     * Sets as check.
     */
    public void setAsCheck() {
        addState(CHECK);
    }

    /**
     * Sets as promotion.
     */
    public void setAsPromotion() {
        addState(State.PROMOTING);
    }

    /**
     * Sets as movable.
     */
    public void setAsMovable() {
        addState(State.CAN_MOVE_TO);
    }

    /**
     * Sets as capture.
     */
    public void setAsCapture() {
        addState(State.CAPTURE);
    }

    /**
     * Gets piece.
     *
     * @return the piece
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Sets piece.
     *
     * @param piece the piece
     */
    public void setPiece(Piece piece) {
        view.syncAction(() -> {
            this.piece = piece;
            setOgQualityIcon(IconManager.getPieceIcon(piece));
        });
    }

    /**
     * Sets piece.
     *
     * @param prevBtn the prev btn
     */
    public void setPiece(BoardButton prevBtn) {
        setPiece(prevBtn.piece);
    }

    /**
     * Gets btn loc.
     *
     * @return the btn loc
     */
    public ViewLocation getBtnLoc() {
        return btnLoc;
    }

    @Override
    public void doClick() {
//        super.doClick();
    }

    @Override
    public void doClick(int pressTime) {

//        super.doClick(pressTime);
    }

    @Override
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        super.setDisabledIcon(icon);
    }

    /**
     * Sets locked.
     *
     * @param lock the lock
     */
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

    /**
     * Start hover.
     */
    public void startHover() {
        addState(State.HOVERED);
    }

    /**
     * Gets icon height.
     *
     * @return the icon height
     */
    private int getIconHeight() {
        if (getHeight() > getWidth())
            return getIconWidth();
        if (getHeight() != 0) {
            iconSize = (int) (getHeight() * ICON_MULTIPLIER);
        }
        return iconSize;
    }

    /**
     * Gets icon width.
     *
     * @return the icon width
     */
    private int getIconWidth() {
        if (getWidth() > getHeight())
            return getIconHeight();
        if (getWidth() != 0) {
            iconSize = (int) (getWidth() * ICON_MULTIPLIER);
        }
        return iconSize;
    }

    /**
     * Sets og quality icon.
     *
     * @param ogQualityIcon the og quality icon
     */
    private void setOgQualityIcon(Icon ogQualityIcon) {
        this.ogQualityIcon = ogQualityIcon;
        setIcon(ogQualityIcon);
        scaleIcon();
    }

    /**
     * Scale icon.
     */
    public synchronized void scaleIcon() {
        if (getIcon() == null || ogQualityIcon == null) return;
//        if (getIcon().getIconWidth() == getIconWidth() && getIcon().getIconHeight() == getIconHeight()) {
//            setDisabledIcon(getIcon());
//            return;
//        }
        ImageIcon newIcon = IconManager.copyImage(ogQualityIcon);
        newIcon = IconManager.scaleImage(newIcon, getIconSize());
        super.setIcon(newIcon);
        super.setDisabledIcon(newIcon);
    }

    /**
     * Gets icon size.
     *
     * @return the icon size
     */
    private Size getIconSize() {
        return new Size(getIconWidth(), getIconHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        singleGenEff.get(btnState).forEach(callback -> callback.callback(g));
    }

    /**
     * Toggle selected.
     */
    public void toggleSelected() {
        if (toggle(SELECTED))
            captureSelectedClr();
    }

    /**
     * Toggle boolean.
     *
     * @param state the state
     * @return true if after toggling the state is on. false otherwise. (=!old is(state))
     */
    public boolean toggle(int state) {
        btnState ^= state;
        return is(state);
    }

    /**
     * Capture selected clr.
     */
    private void captureSelectedClr() {
        selectedClr = view.getBoardPnl().getBoardOverlay().currentColor();
    }

    /**
     * Click me.
     */
    public void clickMe() {
//        System.out.println("clicking " + this);
        view.boardButtonPressed(btnLoc);
    }

    @Override
    public String toString() {
        return "BoardButton{" +
                "btnLoc=" + btnLoc +
                ", btnState=" + StrUtils.bitsStr(btnState) +
                ", piece=" + piece +
                '}';
    }

    /**
     * is this button a button a piece Can move to.
     *
     * @return the boolean
     */
    public boolean canMoveTo() {
        return is(State.CAN_MOVE_TO | State.CAPTURE);
    }

    /**
     * Reset.
     */
    public void reset() {
        setIcon(null);
        resetBackground();
        hiddenIcon = null;
        piece = null;
    }

    /**
     * Moving from. sets this button as a 'moving from' btn to highlight the square
     */
    public void movingFrom() {
        addState(State.MOVING_FROM);
    }

    /**
     * Moving to.
     */
    public void movingTo() {
        addState(State.MOVING_TO);
    }

    /**
     * allows a button to draw on the global, full board's 'canvas'
     *
     * @param g2               the g 2
     * @param mouseCoordinates the mouse coordinates
     * @param c                the c
     */
    public void globalPaint(Graphics2D g2, Point mouseCoordinates, Component c) {
        if (is(State.DRAGGING)) {
            int x = mouseCoordinates.x - getHeight() / 2;
            int y = mouseCoordinates.y - getWidth() / 2;
            if (getHiddenIcon() != null)
                getHiddenIcon().paintIcon(c, g2, x, y);
        }
    }

    /**
     * Gets hidden icon.
     *
     * @return the hidden icon
     */
    public Icon getHiddenIcon() {
        return hiddenIcon;
    }

}
