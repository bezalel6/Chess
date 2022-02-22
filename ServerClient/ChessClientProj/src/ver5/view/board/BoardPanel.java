package ver5.view.board;

import ver5.SharedClasses.FontManager;
import ver5.SharedClasses.Location;
import ver5.SharedClasses.board_setup.Board;
import ver5.SharedClasses.board_setup.Square;
import ver5.SharedClasses.pieces.Piece;
import ver5.SharedClasses.ui.MyLbl;
import ver5.view.View;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.IntStream;

public class BoardPanel extends JPanel implements Iterable<BoardButton[]> {
    private final Dimension btnDimension = new Dimension(100, 100);

    private final int rows, cols;
    private final View view;
    private final Color brown = new Color(79, 60, 33, 255);
    private final Color white = new Color(222, 213, 187);
    private BoardButton[][] btnMat;
    private JPanel buttonsPnl;
    private JPanel colsCoordinatesPnl, rowsCoordinatesPnl;
    private LayerUI<JPanel> layerUI;


    public BoardPanel(int rows, int cols, View view) {
        this.rows = rows;
        this.cols = cols;
        this.view = view;
        buttonsPnl = new JPanel();
        btnMat = new BoardButton[rows][cols];
        buttonsPnl.setLayout(new GridLayout(rows, cols));
        setLayout(new GridBagLayout());
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

        for (int i = 0; i < 8; i++) {
            MyLbl col = new MyLbl(cols.get(i) + "") {{
                setPreferredSize(btnDimension);
                setFont(FontManager.coordinates);
            }};
            MyLbl row = new MyLbl(rows.get(i) + "") {{
                setPreferredSize(btnDimension);
                setFont(FontManager.coordinates);
            }};
            colsCoordinatesPnl.add(col);
            rowsCoordinatesPnl.add(row);
        }
        colsCoordinatesPnl.setPreferredSize(new Dimension(btnDimension.width * 8, btnDimension.height * 8));
        rowsCoordinatesPnl.setPreferredSize(new Dimension(btnDimension.width * 8, btnDimension.height * 8));

    }

    private boolean isBoardFlipped() {
        return view != null && view.isBoardFlipped();
    }

    public void lock(boolean lock) {
        forEachBtnParallel(btn -> btn.setLocked(lock));
        getBoardOverlay().setBlockBoard(lock);
    }

    public void forEachBtnParallel(b call) {
        forEachRowParallel(row -> Arrays.stream(row).parallel().forEach(call::func));
    }

    public BoardOverlay getBoardOverlay() {
        return (BoardOverlay) layerUI;
    }

    public void forEachRowParallel(a call) {
        Arrays.stream(btnMat).parallel().forEach(call::func);
//        Arrays.stream(btnMat).forEach(call::func);
    }

    public void forEachBtn(b call) {
        forEachRow(row -> Arrays.stream(row).forEach(call::func));
    }

    public void forEachRow(a call) {
        Arrays.stream(btnMat).forEach(call::func);
    }

    public void boardContainerSetup() {
        //הוספת הקורדינאטות
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.NONE;
        gbc.fill = GridBagConstraints.BOTH;
        add(colsCoordinatesPnl, gbc);

        gbc = new GridBagConstraints();
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weighty = GridBagConstraints.NONE;
        gbc.fill = GridBagConstraints.BOTH;
        add(rowsCoordinatesPnl, gbc);

        layerUI = new BoardOverlay(view);
        JLayer<JPanel> jlayer = new JLayer<>(buttonsPnl, layerUI);

        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weighty = gbc.weightx = 30;
        gbc.fill = GridBagConstraints.BOTH;
        add(jlayer, gbc);
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
                BoardButton currentBtn = new BoardButton(btnLoc, isBlack ? brown : white, view) {{
                    setSize(btnDimension);
                    setPreferredSize(btnDimension);
                    setFont(FontManager.boardButtons);
                    addActionListener(e -> {
                        view.boardButtonPressed(((BoardButton) e.getSource()).getBtnLoc());
                    });
                    setEnabled(false);
                }};
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
        btnMat[loc.getRow()][loc.getCol()] = button;
    }

    public void restoreBoardButtons(ViewSavedBoard savedBoard) {
        resetAllButtons(true);
        savedBoard.savedSquares.forEach(square -> square.restore(getBtn(square.getLoc())));
        resizeIcons();

    }

    public BoardButton getBtn(Location loc) {
        return getBtn(loc.getRow(), loc.getCol());
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

    public void resizeIcons() {
        forEachBtnParallel(BoardButton::scaleIcon);
    }

    public BoardButton[][] getBtnMat() {
        return btnMat;
    }

    @Override
    public Iterator<BoardButton[]> iterator() {
        return Arrays.stream(btnMat).iterator();
    }

    public interface a {
        void func(BoardButton[] row);
    }

    public interface b {
        void func(BoardButton btn);
    }
}
