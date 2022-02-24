package ver13.view.Board;

import ver13.SharedClasses.Callbacks.Callback;
import ver13.SharedClasses.FontManager;
import ver13.SharedClasses.Location;
import ver13.SharedClasses.PlayerColor;
import ver13.SharedClasses.board_setup.Board;
import ver13.SharedClasses.board_setup.Square;
import ver13.SharedClasses.pieces.Piece;
import ver13.SharedClasses.ui.MyLbl;
import ver13.view.IconManager.Size;
import ver13.view.View;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.IntStream;

public class BoardPanel extends JPanel implements Iterable<BoardButton[]> {
    public static final Color blackSquareClr = new Color(79, 60, 33, 255);
    public static final Color whiteSquareClr = new Color(222, 213, 187);
    private final static Dimension btnDimension = new Size(50);
    private final static Insets coordinatesInsets = new Insets(1, 1, 1, 1);
    private final int rows, cols;
    private final View view;
    private final JPanel me;
    private BoardButton[][] btnMat;
    private JPanel buttonsPnl;
    private JPanel colsCoordinatesPnl, rowsCoordinatesPnl;
    private LayerUI<JPanel> layerUI;
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
            MyLbl col = new Lbl(cols.get(i) + "");
            MyLbl row = new Lbl(rows.get(i) + "");
            colsCoordinatesPnl.add(col);
            rowsCoordinatesPnl.add(row);
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
        forEachRowParallel(row -> Arrays.stream(row).parallel().forEach(call::callback));
    }

    public BoardOverlay getBoardOverlay() {
        return (BoardOverlay) layerUI;
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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = coordinatesInsets;
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.NONE;
        gbc.fill = GridBagConstraints.BOTH;
        me.add(colsCoordinatesPnl, gbc);

        gbc = new GridBagConstraints();
        gbc.insets = coordinatesInsets;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.fill = GridBagConstraints.BOTH;
        me.add(rowsCoordinatesPnl, gbc);

        layerUI = new BoardOverlay(view);
        JLayer<JPanel> jlayer = new JLayer<>(buttonsPnl, layerUI);

        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weighty = gbc.weightx = 30;
        gbc.fill = GridBagConstraints.BOTH;
        me.add(jlayer, gbc);
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
        setCoordinates(false);
        createMat();
        getButtonsPnl().repaint();
        getBoardOverlay().repaintLayer();
    }

    public void createMat() {
        boolean isBlack = false;
        buttonsPnl.removeAll();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Location btnLoc = Location.getLoc(i, j, isBoardFlipped());
                BoardButton currentBtn = new BoardButton(btnLoc, isBlack ? blackSquareClr : whiteSquareClr, view) {
                    {
                        setSize(btnDimension);
//                    setMinimumSize(getPreferredSize());
                        setFont(FontManager.boardButtons);
                        addActionListener(e -> {
                            view.boardButtonPressed(((BoardButton) e.getSource()).getBtnLoc());
                        });
                        setEnabled(false);
                    }

                    @Override
                    public void setSize(int width, int height) {
                        width = height = Math.min(width, height);
                        super.setSize(width, height);
                    }
                };
                setButton(currentBtn, btnLoc);
                buttonsPnl.add(currentBtn);
                isBlack = !isBlack;
            }
            isBlack = !isBlack;
        }
    }

    public JPanel getButtonsPnl() {
        return buttonsPnl;
    }

    private void setButton(BoardButton button, Location loc) {
        btnMat[loc.row][loc.col] = button;
    }

    public void restoreBoardButtons(ViewSavedBoard savedBoard) {
        resetAllButtons(true);
        savedBoard.savedSquares.forEach(square -> square.restore(getBtn(square.getLoc())));
        resizeIcons();
    }

    public BoardButton getBtn(Location loc) {
        return getBtn(loc.row, loc.col);
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
        getBoardOverlay().repaintLayer();
//        setOtbMode(true);
        repaint();
    }

    @Override
    public void repaint() {
        super.repaint();
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

    public void setOtbMode(boolean state) {
        for (BoardButton[] row : this) {
            for (BoardButton btn : row) {
                Piece piece = btn.getPiece();
                if (piece != null && piece.isOnMyTeam(PlayerColor.BLACK)) {

                }
            }
        }
    }

    public interface BtnRowCallback extends Callback<BoardButton[]> {
    }

    public interface BtnCallBack extends Callback<BoardButton> {
    }
}
