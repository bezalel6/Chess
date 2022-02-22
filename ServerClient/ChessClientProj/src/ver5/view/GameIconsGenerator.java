package ver5.view;

import ver5.Client;
import ver5.SharedClasses.board_setup.Board;
import ver5.view.board.BoardPanel;

import javax.swing.*;

public class GameIconsGenerator {

    public static void main(String[] args) {
        new JFrame() {{
            setSize(500, 500);
            add(new JLabel(GameIconsGenerator.generate(Board.example)));
            setVisible(true);
        }};
        new DummyClient(Board.example).runClient();
    }

    public static ImageIcon generate(Board board) {
        return new DummyClient(board).getScreenshot();
    }

    static class DummyClient extends Client {
        private final Board board;

        public DummyClient(Board board) {
            super(false);
            this.board = board;
        }

        @Override
        public void setupClient() {

        }

        public ImageIcon getScreenshot() {
            ImageIcon ret = screenshot();
            closeClient();
            return ret;
        }

        private ImageIcon screenshot() {
            BoardPanel boardPanel = getView().getBoardPnl();
            boardPanel.setBoardButtons(board);
//        boardPanel.resetOrientation();
            return IconManager.screenshot(boardPanel);
        }
    }
}
