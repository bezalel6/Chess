package ver14.view.Board;

import org.intellij.lang.annotations.MagicConstant;
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

import static ver14.view.Board.BoardButton.State.*;

public class BoardButton extends MyJButton {
    private static final double ICON_MULTIPLIER = .8;
    private final static Color CHECK_COLOR = new Color(186, 11, 11, 255);
    private final static Color CAPTURE_COLOR = new Color(0, 0, 0, 255 / 4);
    private final static Color PROMOTING_COLOR = new Color(151, 109, 3);
    private static final Cursor HOVERED_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private static int iconSize = 50;
    private final MyColor startingBackgroundColor;
    private final View view;
    private final ViewLocation btnLoc;
    private final Map<Integer, Callback<Graphics>> statesCallbacks = new HashMap<>();
    private final EfficientGen<Integer, ArrayList<Callback<Graphics>>> singleGenEff;
    private int btnState;
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

    private void setCallback(@State int state, Callback<Graphics> callback) {
        statesCallbacks.put(bitIndex(state), callback);
    }

    public void hideIcon() {
        hiddenIcon = getIcon();
        setIcon(null);
    }

    public boolean is(@State int state) {
        return (btnState & state) != 0;
    }

    public void unHideIcon() {
        setIcon(hiddenIcon);
        hiddenIcon = null;
    }

    public static int bitIndex(int powOf2) {
        return (int) MathUtils.log(powOf2, 2);
    }

    public void endHover() {
        removeState(State.HOVERED);
    }

    public void removeState(int removing) {
        btnState &= ~(removing);
        repaint();
    }

    public void setStates(int states) {
        resetBackground();
        btnState = (states);
        repaint();
    }

    public void resetBackground() {
        resetStates();
        setBackground(startingBackgroundColor);
        if (view != null)
            view.repaint();
    }

    private void resetStates() {
        btnState = 0;
        repaint();
    }

    public int getBtnState() {
        return btnState;
    }

    public void setAsCurrent() {
        addState(State.CURRENT);
    }

    public void addState(int adding) {
        btnState |= (adding);
        repaint();
    }

    public void setAsCheck() {
        addState(CHECK);
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
            iconSize = (int) (getHeight() * ICON_MULTIPLIER);
        }
        return iconSize;
    }

    private int getIconWidth() {
        if (getWidth() > getHeight())
            return getIconHeight();
        if (getWidth() != 0) {
            iconSize = (int) (getWidth() * ICON_MULTIPLIER);
        }
        return iconSize;
    }

    private void setOgQualityIcon(Icon ogQualityIcon) {
        this.ogQualityIcon = ogQualityIcon;
        setIcon(ogQualityIcon);
        scaleIcon();
    }

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

    private Size getIconSize() {
        return new Size(getIconWidth(), getIconHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        singleGenEff.get(btnState).forEach(callback -> callback.callback(g));
    }

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

    private void captureSelectedClr() {
        selectedClr = view.getBoardPnl().getBoardOverlay().currentColor();
    }

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

    public boolean canMoveTo() {
        return is(State.CAN_MOVE_TO | State.CAPTURE);
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

    /**
     * allows a button to draw on the global, full board's 'canvas'
     *
     * @param g2
     * @param mouseCoordinates
     */
    public void globalPaint(Graphics2D g2, Point mouseCoordinates, Component c) {
        if (is(State.DRAGGING)) {
            int x = mouseCoordinates.x - getHeight() / 2;
            int y = mouseCoordinates.y - getWidth() / 2;
            if (getHiddenIcon() != null)
                getHiddenIcon().paintIcon(c, g2, x, y);
        }
    }

    public Icon getHiddenIcon() {
        return hiddenIcon;
    }

    @MagicConstant(flagsFromClass = State.class)
    public @interface State {
        int CHECK = 1;
        int CAPTURE = 2;
        int CAN_MOVE_TO = 4;
        int CURRENT = 8;
        int PROMOTING = 16;
        int MOVING_FROM = 32;
        int MOVING_TO = 64;
        int HOVERED = 128;
        int CLICKED_ONCE = 256;
        int DRAGGING = 512;
        int SELECTED = 1024;


    }
}
