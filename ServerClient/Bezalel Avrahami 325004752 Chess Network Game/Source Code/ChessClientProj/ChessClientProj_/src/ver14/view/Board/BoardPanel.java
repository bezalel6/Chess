package ver14.view.Board;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Square;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.UI.MyLbl;
import ver14.view.IconManager.Size;
import ver14.view.View;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * represents the Board panel. holding all the {@link BoardButton}s.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class BoardPanel extends JPanel implements Iterable<BoardButton[]> {
    /**
     * The constant blackSquareClr.
     */
    public static final MyColor blackSquareClr = new MyColor("#71828f");
    /**
     * The constant whiteSquareClr.
     */
    public static final MyColor whiteSquareClr = new MyColor("#f2f5f3");
    /**
     * The constant btnDimension.
     */
    private final static Dimension btnDimension = new Size(50);
    /**
     * The constant coordinatesInsets.
     */
    private final static Insets coordinatesInsets = new Insets(1, 1, 1, 1);
    /**
     * The View.
     */
    private final View view;
    /**
     * The Me.
     */
    private final JPanel me;
    /**
     * The Btn mat.
     */
    private BoardButton[][] btnMat;
    /**
     * The Buttons pnl.
     */
    private JPanel buttonsPnl;
    /**
     * The Cols coordinates pnl.
     */
    private JPanel colsCoordinatesPnl, /**
     * The Rows coordinates pnl.
     */
    rowsCoordinatesPnl;
    /**
     * The Board overlay.
     */
    private BoardOverlay boardOverlay;
    private ViewSavedBoard actualPosition = null;

    /**
     * Instantiates a new Board panel.
     *
     * @param rows the rows
     * @param cols the cols
     * @param view the view
     */
    public BoardPanel(int rows, int cols, View view) {
        this.me = new JPanel(new GridBagLayout());
        setLayout(new GridBagLayout());
        add(me, new GridBagConstraints());
        this.view = view;
        buttonsPnl = new JPanel();
        btnMat = new BoardButton[rows][cols];
        buttonsPnl.setLayout(new GridLayout(rows, cols));
        setCoordinates();
    }

    /**
     * Sets coordinates.
     */
    private void setCoordinates() {
        setCoordinates(true);
    }

    /**
     * Sets coordinates.
     *
     * @param initialize should initialize
     */
    public void setCoordinates(boolean initialize) {
        if (initialize) {
            colsCoordinatesPnl = new JPanel();
            rowsCoordinatesPnl = new JPanel();

            colsCoordinatesPnl.setLayout(new GridLayout(1, 8));
            rowsCoordinatesPnl.setLayout(new GridLayout(8, 1));
        } else {
            colsCoordinatesPnl.removeAll();
            rowsCoordinatesPnl.removeAll();
        }
        ArrayList<Integer> rows = new ArrayList<>();

        ArrayList<Character> cols = new ArrayList<>();
        IntStream.range(0, 8).forEach(i -> {
            rows.add(i + 1);
            cols.add((char) (i + 'a'));
        });
        if (!isBoardFlipped()) {
            Collections.reverse(rows);
        } else {
            Collections.reverse(cols);
        }
        class Lbl extends MyLbl {
            public Lbl(String text) {
                super(text);
                setPreferredSize(getMinimumSize());
                setFont(FontManager.coordinates);
            }
        }
        for (int i = 0; i < 8; i++) {
            colsCoordinatesPnl.add(new Lbl(cols.get(i) + ""));
            rowsCoordinatesPnl.add(new Lbl(rows.get(i) + ""));
        }
        FontMetrics metrics = getFontMetrics(FontManager.coordinates);
        int small = Math.max(metrics.getHeight(), metrics.stringWidth("a"));
        colsCoordinatesPnl.setPreferredSize(new Dimension(btnDimension.width * 8, small));
        rowsCoordinatesPnl.setPreferredSize(new Dimension(small, btnDimension.height * 8));

    }

    /**
     * Is board flipped boolean.
     *
     * @return the boolean
     */
    private boolean isBoardFlipped() {
        return view != null && view.isBoardFlipped();
    }

    public static MyColor getClr(PlayerColor plr) {
        return plr == PlayerColor.WHITE ? whiteSquareClr : blackSquareClr;
    }

    /**
     * Sets all sizes.
     *
     * @param comp the comp
     * @param w    the w
     * @param h    the h
     */
    static void setAllSizes(JComponent comp, double w, double h) {
        setAllSizes(comp, (int) w, (int) h);
    }

    /**
     * Sets all sizes.
     *
     * @param comp the comp
     * @param w    the w
     * @param h    the h
     */
    static void setAllSizes(JComponent comp, int w, int h) {
        Size size = new Size(w, h);
        comp.setSize(size);
        comp.setPreferredSize(size);
        comp.setMaximumSize(size);
        comp.setMinimumSize(size);
    }

    /**
     * Lock all buttons.
     *
     * @param lock the lock
     */
    public void lock(boolean lock) {
        forEachBtnParallel(btn -> btn.setLocked(lock));
        getBoardOverlay().setBlockBoard(lock);
    }

    /**
     * For each btn parallel.
     *
     * @param call the call
     */
    public void forEachBtnParallel(BtnCallBack call) {
        view.syncAction(() -> {
            forEachRowParallel(row -> Arrays.stream(row).parallel().forEach(call::callback));
        });
    }

    /**
     * Gets board overlay.
     *
     * @return the board overlay
     */
    public BoardOverlay getBoardOverlay() {
        return boardOverlay;
    }

    /**
     * For each row parallel.
     *
     * @param call the call
     */
    public void forEachRowParallel(BtnRowCallback call) {
        Arrays.stream(btnMat).parallel().forEach(call::callback);
//        Arrays.stream(btnMat).forEach(call::func);
    }

    /**
     * For each btn.
     *
     * @param call the call
     */
    public void forEachBtn(BtnCallBack call) {
        forEachRow(row -> Arrays.stream(row).forEach(call::callback));
    }

    /**
     * For each row.
     *
     * @param call the call
     */
    public void forEachRow(BtnRowCallback call) {
        Arrays.stream(btnMat).forEach(call::callback);
    }

    /**
     * Board container setup.
     */
    public void boardContainerSetup() {
        //הוספת הקורדינאטות
        GridBagConstraints gbc;

        gbc = new GridBagConstraints();
        gbc.insets = coordinatesInsets;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.fill = GridBagConstraints.BOTH;
        me.add(rowsCoordinatesPnl, gbc);

        boardOverlay = new BoardOverlay(view);
        JLayer<JPanel> jlayer = new JLayer<>(buttonsPnl, boardOverlay);

        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.RELATIVE;
        gbc.weighty = gbc.weightx = 30;
        gbc.fill = GridBagConstraints.BOTH;
        me.add(jlayer, gbc);

        gbc = new GridBagConstraints();
        gbc.insets = coordinatesInsets;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridheight = 1;
        me.add(colsCoordinatesPnl, gbc);
    }

    /**
     * Sets board buttons.
     *
     * @param board the board
     */
    public void setBoardButtons(Board board) {
        resetAllButtons(true);
        for (Square square : board) {
            if (!square.isEmpty()) {
                BoardButton btn = getBtn(square.loc);
                Piece piece = square.getPiece();
                btn.setPiece(piece);
            }
        }
        resizeIcons();
    }

    /**
     * Reset orientation.
     */
    public void resetOrientation() {
        ViewSavedBoard savedBoard = new ViewSavedBoard(this);
        setCoordinates(false);
        createMat();
        loadSaved(savedBoard);
        repaint();
        SwingUtilities.invokeLater(this::resizeIcons);
//        getButtonsPnl().repaint();
//        getBoardOverlay().repaintLayer();
    }

    /**
     * Create mat.
     */
    public void createMat() {
        buttonsPnl.removeAll();

        for (Location loc : Location.ALL_LOCS) {
            if (view.isBoardFlipped())
                loc = loc.flip();
            ViewLocation btnLoc = new ViewLocation(loc);

            BoardButton currentBtn = new BoardButton(btnLoc, loc.isBlackSquare() ? blackSquareClr : whiteSquareClr, view) {
                {
                    setSize(btnDimension);
                    addActionListener(e -> {
//                        view.boardButtonPressed(((BoardButton) e.getSource()).getBtnLoc());
                    });
                    setEnabled(false);
//                    setMinimumSize(getPreferredSize());
                }

                @Override
                public void setSize(int width, int height) {
                    width = height = Math.min(width, height);
                    super.setSize(width, height);
                }
            };
            setButton(currentBtn, currentBtn.getBtnLoc());
            buttonsPnl.add(currentBtn);
        }
        repaint();
    }

    /**
     * Sets button.
     *
     * @param button the button
     * @param loc    the loc
     */
    private void setButton(BoardButton button, ViewLocation loc) {
        btnMat[loc.viewLocation.row][loc.viewLocation.col] = button;
    }

    /**
     * Gets buttons pnl.
     *
     * @return the buttons pnl
     */
    public JPanel getButtonsPnl() {
        return buttonsPnl;
    }

    public synchronized void restoreActualPosition() {
        assert actualPosition != null;
        loadSaved(actualPosition);
    }

    /**
     * Restore board buttons.
     *
     * @param savedBoard the saved board
     */
    public synchronized void loadSaved(ViewSavedBoard savedBoard) {
        System.out.println("loading " + savedBoard);
        resetAllButtons(true);
        savedBoard.savedSquares.forEach(square -> square.restore(getBtn(square.getLoc())));

//        resizeIcons();
    }

    /**
     * Reset all buttons.
     *
     * @param resetIcons the reset icons
     */
    public void resetAllButtons(boolean resetIcons) {
        System.out.println("reseting");
        forEachBtnParallel(btn -> {
            btn.resetBackground();
            if (resetIcons) {
                btn.reset();
            }
        });
    }

    /**
     * Gets btn.
     *
     * @param loc the loc
     * @return the btn
     */
    public BoardButton getBtn(ViewLocation loc) {
        return getBtn(loc.viewLocation.row, loc.viewLocation.col);
    }

    /**
     * Gets btn.
     *
     * @param r the r
     * @param c the c
     * @return the btn
     */
    public BoardButton getBtn(int r, int c) {
        return btnMat[r][c];
    }

    public synchronized ViewSavedBoard getActualPosition() {
        System.out.println("this = " + this);
        System.out.println("actual position var = " + actualPosition);
        return actualPosition;
    }

    /**
     * On resize.
     */
    public void onResize() {
//        int diff = lastResize.maxDiff(getSize());
//        if (diff < 5) {
//            return;
//        }
        SwingUtilities.invokeLater(() -> {
            System.out.println("onResize() called");
            int size = Math.min(getWidth(), getHeight());
            setAllSizes(me, size, size);

            setAllSizes(this, getSize());

            view.getWin().pack();
            resizeIcons();
            repaint();
            view.getWin().pack();
        });

    }

    /**
     * Sets all sizes.
     *
     * @param comp the comp
     * @param d    the d
     */
    static void setAllSizes(JComponent comp, Dimension d) {
        setAllSizes(comp, d.width, d.height);
    }

    /**
     * Resize icons.
     */
    public void resizeIcons() {
        System.out.println("resizing icon. pnl size is = " + getSize());
        forEachBtnParallel(BoardButton::scaleIcon);
        repaint();
    }

    @Override
    public void repaint() {
        super.repaint();
        if (buttonsPnl != null)
            buttonsPnl.repaint();
        if (boardOverlay != null)
            boardOverlay.repaintLayer();
        if (view != null)
            view.repaint();
    }

    @Override
    public String toString() {
        return createBoard().toString();
    }

    /**
     * Create board board.
     *
     * @return the board
     */
    public Board createBoard() {
        Board board = new Board();
        for (var loc : Location.ALL_LOCS) {
            board.setPiece(loc, getBtn(loc).getPiece());
        }
        return board;
    }

    /**
     * Gets btn.
     *
     * @param loc the loc
     * @return the btn
     */
    public BoardButton getBtn(Location loc) {
        return getBtn(new ViewLocation(loc));
    }

    /**
     * Get btn mat board button [ ] [ ].
     *
     * @return the board button [ ] [ ]
     */
    public BoardButton[][] getBtnMat() {
        return btnMat;
    }

    @Override
    public Iterator<BoardButton[]> iterator() {
        return Arrays.stream(btnMat).iterator();
    }

    public void newPosition() {
        System.out.println("saving new pos " + this);
        actualPosition = new ViewSavedBoard(this);
    }

    /**
     * Btn row callback.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public interface BtnRowCallback extends Callback<BoardButton[]> {
    }

    /**
     * Btn call back.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public interface BtnCallBack extends Callback<BoardButton> {
    }
}
