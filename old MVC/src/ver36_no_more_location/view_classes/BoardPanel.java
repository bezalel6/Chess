package ver36_no_more_location.view_classes;

import ver36_no_more_location.Location;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.IntStream;

public class BoardPanel extends JPanel implements Iterable<BoardButton[]> {
    private final Font coordinatesFont = new Font(null, Font.BOLD, 30);
    private final Dimension btnDimension = new Dimension(90, 100);

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

    public void createMat() {
        boolean isBlack = false;
        buttonsPnl.removeAll();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Location btnLoc = Location.getLoc(i, j, view.isBoardFlipped());
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

    public BoardOverlay getBoardOverlay() {
        return (BoardOverlay) layerUI;
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
        if (!view.isBoardFlipped()) {
            Collections.reverse(rows);
        } else {
            Collections.reverse(cols);
        }

        for (int i = 0; i < 8; i++) {
            JLabel col = new JLabel(cols.get(i) + "") {{
                setPreferredSize(btnDimension);
                setFont(coordinatesFont);
            }};
            JLabel row = new JLabel(rows.get(i) + "") {{
                setPreferredSize(btnDimension);
                setFont(coordinatesFont);
            }};
            colsCoordinatesPnl.add(col);
            rowsCoordinatesPnl.add(row);
        }
        colsCoordinatesPnl.setPreferredSize(new Dimension(btnDimension.width * 8, btnDimension.height * 8));
        rowsCoordinatesPnl.setPreferredSize(new Dimension(btnDimension.width * 8, btnDimension.height * 8));

    }

    public void boardContainerSetup() {
        //הוספת הקורדינאטות
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        add(colsCoordinatesPnl, gbc);

        gbc = new GridBagConstraints();
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weighty = GridBagConstraints.REMAINDER;
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
