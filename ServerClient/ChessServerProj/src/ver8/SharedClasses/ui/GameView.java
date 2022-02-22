package ver8.SharedClasses.ui;

import ver8.SharedClasses.board_setup.Board;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {
    private final Font font = new Font(null, Font.BOLD, 30);
    private JTextArea textArea;

    public GameView(boolean show) throws HeadlessException {
        textArea = new JTextArea() {{
            setEditable(false);
            setFont(font);
        }};
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(textArea);
        setSize(400, 400);
        setVisible(show);
    }

    public void update(Board board) {
        textArea.setText(board.toString());
    }
}
