package ver14.view.Board;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.Game.BoardSetup.Board;
import ver14.SharedClasses.Game.BoardSetup.Square;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.pieces.Piece;
import ver14.SharedClasses.ui.MyLbl;
import ver14.view.IconManager.Size;
import ver14.view.View;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.IntStream;

public class BoardPanel extends JPanel implements Iterable<BoardButton[]> {
    public static final Color blackSquareClr = Color.decode("#71828f");
    //    public static final Color blackSquareClr = new Color(79, 60, 33, 255);
    public static final Color whiteSquareClr = Color.decode("#f2f5f3");
    //    public static final Color whiteSquareClr = new Color(222, 213, 187);
    private final static Dimension btnDimension = new Size(50);
    private final static Insets coordinatesInsets = new Insets(1, 1, 1, 1);
    private final int rows, cols;
    private final View view;
    private final JPanel me;
    private BoardButton[][] btnMat;
    private JPanel buttonsPnl;
    private JPanel colsCoordinatesPnl, rowsCoordinatesPnl;
    private BoardOverlay boardOverlay;
    private Size lastResize = new Size(0);

    public BoardPanel(int rows, int cols, View view) {
        this.me = new JPanel(new GridBagLayout());
        setLayout(new GridBagLayout());
        add(me, new GridBagConstraints());
        this.rows = rows;
        this.cols = cols;
        this.view = view;
        buttonsPnl = new JPanel();
        btnMat = new BoardButton[rows][cols];
        buttonsPnl.setLayout(new GridLayout(rows, cols));
        setCoordinates();

    }

    private void setCoordinates() {
        setCoordinates(true);
    }

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

    private boolean isBoardFlipped() {
        return view != null && view.isBoardFlipped();
    }

    static void setAllSizes(JComponent comp, double w, double h) {
        setAllSizes(comp, (int) w, (int) h);
    }

    static void setAllSizes(JComponent comp, int w, int h) {
        Size size = new Size(w, h);
        comp.setSize(size);
        comp.setPreferredSize(size);
        comp.setMaximumSize(size);
        comp.setMinimumSize(size);
    }

    public void lock(boolean lock) {
        forEachBtnParallel(btn -> btn.setLocked(lock));
        getBoardOverlay().setBlockBoard(lock);
    }

    public void forEachBtnParallel(BtnCallBack call) {
        synchronized (view.boardLock) {
            forEachRowParallel(row -> Arrays.stream(row).parallel().forEach(call::callback));
        }
    }

    public BoardOverlay getBoardOverlay() {
        return boardOverlay;
    }

    public void forEachRowParallel(BtnRowCallback call) {
        Arrays.stream(btnMat).parallel().forEach(call::callback);
//        Arrays.stream(btnMat).forEach(call::func);
    }

    public void forEachBtn(BtnCallBack call) {
        forEachRow(row -> Arrays.stream(row).forEach(call::callback));
    }

    public void forEachRow(BtnRowCallback call) {
        Arrays.stream(btnMat).forEach(call::callback);
    }

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

    public void setBoardButtons(Board board) {
        resetAllButtons(true);
        for (Square square : board) {
            if (!square.isEmpty()) {
                BoardButton btn = getBtn(square.getLoc());
                Piece piece = square.getPiece();
                btn.setPiece(piece);
            }
        }
        resizeIcons();
    }

    public void resetOrientation() {
        ViewSavedBoard savedBoard = new ViewSavedBoard(this);
        setCoordinates(false);
        createMat();
        restoreBoardButtons(savedBoard);
        repaint();
        SwingUtilities.invokeLater(this::resizeIcons);
//        getButtonsPnl().repaint();
//        getBoardOverlay().repaintLayer();
    }

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
                        view.boardButtonPressed(((BoardButton) e.getSource()).getBtnLoc());
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

    private void setButton(BoardButton button, ViewLocation loc) {
        btnMat[loc.viewLocation.row][loc.viewLocation.col] = button;
    }

    public JPanel getButtonsPnl() {
        return buttonsPnl;
    }

    public void restoreBoardButtons(ViewSavedBoard savedBoard) {
        resetAllButtons(true);
        savedBoard.savedSquares.forEach(square -> square.restore(getBtn(square.getLoc())));
        resizeIcons();
    }

    public BoardButton getBtn(Location loc) {
        return getBtn(new ViewLocation(loc));
    }

    public BoardButton getBtn(ViewLocation loc) {
        return getBtn(loc.viewLocation.row, loc.viewLocation.col);
    }

    public BoardButton getBtn(int r, int c) {
        return btnMat[r][c];
    }

    public void resetAllButtons(boolean resetIcons) {
        forEachBtnParallel(btn -> {
            btn.resetBackground();
            if (resetIcons) {
                btn.reset();
            }
        });
    }

    public void onResize() {
        int diff = lastResize.maxDiff(getSize());
        if (diff < 5) {
            return;
        }
        lastResize = new Size(getSize());
        int size = Math.min(getWidth(), getHeight());
        setAllSizes(me, size, size);

        setAllSizes(this, getSize());

        view.getWin().pack();
        resizeIcons();
        repaint();

    }

    static void setAllSizes(JComponent comp, Dimension d) {
        setAllSizes(comp, d.width, d.height);
    }

    public void resizeIcons() {
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

    public BoardButton[][] getBtnMat() {
        return btnMat;
    }

    @Override
    public Iterator<BoardButton[]> iterator() {
        return Arrays.stream(btnMat).iterator();
    }

    public interface BtnRowCallback extends Callback<BoardButton[]> {
    }

    public interface BtnCallBack extends Callback<BoardButton> {
    }
}
