package ver14.SharedClasses.UI;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;

import javax.swing.*;
import java.awt.*;


/**
 * Game view - represents a debugging window for viewing a game's status.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class GameView extends JFrame {
    /**
     * The Font.
     */
    private final Font font = new Font(null, Font.BOLD, 30);
    /**
     * The Text area.
     */
    private JTextArea textArea;

    /**
     * Instantiates a new Game view.
     *
     * @throws HeadlessException the headless exception
     */
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

    /**
     * Update.
     *
     * @param board the board
     */
    public void update(Board board) {
        textArea.setText(board.toString());
    }
}
