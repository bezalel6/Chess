package ver9.view;

import ver9.SharedClasses.Location;
import ver9.SharedClasses.PlayerColor;
import ver9.SharedClasses.RegEx;
import ver9.SharedClasses.board_setup.Board;
import ver9.SharedClasses.board_setup.Square;
import ver9.SharedClasses.pieces.Piece;
import ver9.view.Board.BoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GameIconsGenerator {

    public static void main(String[] args) {
        new JFrame() {{
            setSize(500, 500);
            Board b = new Board();
            b.fenSetup("rnbqkbnr/pppp1ppp/8/8/4Pp2/8/PPPP2PP/RNBQKBNR w KQkq - 0 3");
            add(new JLabel(GameIconsGenerator.generate(b, PlayerColor.BLACK, new IconManager.Size(400, 400))));
            setVisible(true);
        }};
    }

    public static ImageIcon generate(Board board, PlayerColor boardOrientation, Dimension iconSize) {
        return new Generator(board, boardOrientation, iconSize).getScreenshot();
    }

    public static ImageIcon generate(String fen, PlayerColor boardOrientation, Dimension iconSize) {
        if (!RegEx.Fen.check(fen)) {
            return IconManager.scaleImage(IconManager.redX, iconSize);
        }

        return generate(new Board(fen), boardOrientation, iconSize);
    }

    static class Generator {
        private final PlayerColor boardOrientation;
        private final Dimension iconSize;
        private final Board board;

        public Generator(Board board, PlayerColor boardOrientation, Dimension iconSize) {
            this.board = board;
            this.boardOrientation = (boardOrientation == null || boardOrientation == PlayerColor.NO_PLAYER) ? PlayerColor.WHITE : boardOrientation;
            this.iconSize = IconManager.Size.minSize(iconSize);
        }


        public ImageIcon getScreenshot() {
            ImageIcon ret = screenshot();
            return ret;
        }

        private ImageIcon screenshot() {
            int w = iconSize.width;
            int h = iconSize.height;
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();

            IconManager.Size squareSize = new IconManager.Size(iconSize);
            squareSize.multBy((double) 1 / 8);
            IconManager.Size pieceSize = new IconManager.Size(squareSize);
            pieceSize.multBy(0.8);

            for (Square square : board) {
                Location loc = square.getLoc();
                if (boardOrientation != PlayerColor.WHITE) {
                    int r = Location.getFlipped(loc.getRow());
                    int c = Location.getFlipped(loc.getCol());
                    loc = Location.getLoc(r, c);
                }
                int x = loc.getCol() * squareSize.width;
                int y = loc.getRow() * squareSize.height;
                Color bg = loc.isWhiteSquare() ? BoardPanel.whiteSquareClr : BoardPanel.blackSquareClr;
                g2d.setColor(bg);
                g2d.fillRect(x, y, squareSize.width, squareSize.height);
                Piece piece = square.getPiece();
                ImageIcon icon = IconManager.getPieceIcon(piece);
                if (icon != null) {
                    int d = 8;
                    x += squareSize.width / d;
                    y += squareSize.height / d;
                    icon = IconManager.scaleImage(icon, pieceSize);
                    g2d.drawImage(icon.getImage(), x, y, null);
                }
            }

            return new ImageIcon(image);
        }
    }
}
