package ver6.view;

import ver6.Client;
import ver6.SharedClasses.PlayerColor;
import ver6.SharedClasses.board_setup.Board;
import ver6.view.board.BoardPanel;

import javax.swing.*;

public class GameIconsGenerator {

    public static void main(String[] args) {
        new JFrame() {{
            setSize(500, 500);
            Board b = new Board();
            b.fenSetup("rnbqkbnr/pppp1ppp/8/8/4Pp2/8/PPPP2PP/RNBQKBNR w KQkq - 0 3");
            add(new JLabel(GameIconsGenerator.generate(b, PlayerColor.WHITE, new IconManager.Size(200, 200))));
            setVisible(true);
        }};
//        new DummyClient(Board.example).runClient();
    }

    public static ImageIcon generate(Board board, PlayerColor boardOrientation, IconManager.Size iconSize) {
        return new DummyClient(board, boardOrientation, iconSize).getScreenshot();
    }

    public static ImageIcon generate(String fen, PlayerColor boardOrientation, IconManager.Size iconSize) {
        return generate(new Board(fen), boardOrientation, iconSize);
    }

    static class DummyClient extends Client {
        private final Board board;
        private final PlayerColor boardOrientation;
        private final IconManager.Size iconSize;

        public DummyClient(Board board, PlayerColor boardOrientation, IconManager.Size iconSize) {
            super(false);
            this.board = board;
            this.boardOrientation = boardOrientation;
            this.iconSize = iconSize;
        }

        @Override
        public void setupClient() {

        }

        @Override
        public void closeClient(String... cause) {
            closeView();
        }

        @Override
        public void runClient() {
        }

        public ImageIcon getScreenshot() {
            ImageIcon ret = screenshot();
            disconnectedFromServer();
            return ret;
        }

        private ImageIcon screenshot() {
            View view = getView();
            BoardPanel boardPanel = view.getBoardPnl();
            boardPanel.setSize(iconSize);
            boardPanel.setPreferredSize(iconSize);

            view.setBoardOrientation(boardOrientation);
            boardPanel.setBoardButtons(board);

            return IconManager.screenshot(boardPanel.getBoardOverlay(), iconSize);
        }
    }
}
