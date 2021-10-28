package ver31_fast_minimax.view_classes;

import ver31_fast_minimax.Location;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;

public class BoardPanel extends JPanel implements Iterable<BoardButton[]> {

    private final int rows, cols;
    private final View view;
    private final Color brown = new Color(79, 60, 33, 255);
    private final Color white = new Color(222, 213, 187);
    private BoardButton[][] btnMat;
    private JPanel buttonsPnl;

    public BoardPanel(int rows, int cols, View view) {
        this.rows = rows;
        this.cols = cols;
        this.view = view;
        buttonsPnl = new JPanel();
        btnMat = new BoardButton[rows][cols];
        buttonsPnl.setLayout(new GridLayout(rows, cols));
    }

    public void createMat() {
        boolean isBlack = false;
        buttonsPnl.removeAll();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Location btnLoc = new Location(i, j, view.isBoardFlipped());
                BoardButton currentBtn = new BoardButton(btnLoc, isBlack ? brown : white);
                currentBtn.setFont(new Font(null, Font.BOLD, 50));
                currentBtn.addActionListener(e -> {
                    view.boardButtonPressed(((BoardButton) e.getSource()).getBtnLoc());
                });
                setButton(currentBtn, btnLoc);
                buttonsPnl.add(currentBtn);
                isBlack = !isBlack;
            }
            isBlack = !isBlack;
        }
    }

    public BoardButton[][] getBtnMat() {
        return btnMat;
    }

    public BoardButton getBtn(int r, int c) {
        return btnMat[r][c];
    }

    public JPanel getButtonsPnl() {
        return buttonsPnl;
    }

    private void setButton(BoardButton button, Location loc) {
        btnMat[loc.getRow()][loc.getCol()] = button;
    }

    public void resetAllButtons(boolean resetIcons) {
        for (BoardButton[] row : this) {
            for (BoardButton btn : row) {
                btn.resetBackground();
                if (resetIcons) {
                    btn.resetIcon();
                }
            }
        }
    }

    @Override
    public Iterator<BoardButton[]> iterator() {
        return Arrays.stream(btnMat).iterator();
    }
}
