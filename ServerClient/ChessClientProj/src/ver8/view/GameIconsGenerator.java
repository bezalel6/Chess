package ver8.view;

import ver8.Client;
import ver8.SharedClasses.PlayerColor;
import ver8.SharedClasses.RegEx;
import ver8.SharedClasses.board_setup.Board;
import ver8.view.Board.BoardPanel;

import javax.swing.*;
import java.awt.*;

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

    public static ImageIcon generate(Board board, PlayerColor boardOrientation, Dimension iconSize) {
        return new DummyClient(board, boardOrientation, iconSize).getScreenshot();
    }

    public static ImageIcon generate(String fen, PlayerColor boardOrientation, Dimension iconSize) {
        if (!RegEx.Fen.check(fen)) {
            return IconManager.redX;
        }
        return generate(new Board(fen), boardOrientation, iconSize);
    }

    static class DummyClient extends Client {
        private final Board board;
        private final PlayerColor boardOrientation;
        private final Dimension iconSize;

        public DummyClient(Board board, PlayerColor boardOrientation, Dimension iconSize) {
            super(false);
            this.board = board;
            this.boardOrientation = (boardOrientation == null || boardOrientation == PlayerColor.NO_PLAYER) ? PlayerColor.WHITE : boardOrientation;
//            this.iconSize = new IconManager.Size(550);
            this.iconSize = iconSize;
        }

        @Override
        public void setupClient() {

        }

        @Override
        public void closeClient(String... cause) {
            closeGui();
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

            boardPanel.resizeIcons();
            boardPanel.setBoardButtons(board);
//            view.setBoardOrientation(boardOrientation);
            return IconManager.screenshot(boardPanel.getBoardOverlay(), iconSize);
        }
    }
}
