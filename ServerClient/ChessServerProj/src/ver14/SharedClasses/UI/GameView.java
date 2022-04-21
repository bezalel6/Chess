package ver14.SharedClasses.UI;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {
    private final Font font = new Font(null, Font.BOLD, 30);
    private JTextArea textArea;

    public GameView() throws HeadlessException {
        textArea = new JTextArea() {{
            setEditable(false);
            setFont(font);
        }};
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(textArea);
        setSize(400, 400);
        setVisible(true);
    }

    public void update(Board board) {
        textArea.setText(board.toString());
    }
}
