package ver14.view.IconManager;

import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.Game.BoardSetup.Board;
import ver14.SharedClasses.Game.BoardSetup.Square;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.pieces.Piece;
import ver14.SharedClasses.RegEx;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Board.BoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GameIconsGenerator {

    public static void main(String[] args) {
        new JFrame() {{
            setSize(500, 500);
            add(new JLabel(GameIconsGenerator.generate("rnbqkbnr/pppp1ppp/8/8/4Pp2/8/PPPP2PP/RNBQKBNR w KQkq - 0 3", PlayerColor.BLACK, new Size(400, 400))));
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }};
    }

    public static ImageIcon generate(String fen, PlayerColor boardOrientation, Dimension iconSize) {
        if (!RegEx.Fen.check(fen)) {
            return IconManager.scaleImage(IconManager.redX, iconSize);
        }

        return screenshot(fen, boardOrientation, iconSize);
    }

    private static ImageIcon screenshot(String fen, PlayerColor orientation, Dimension iconSize) {
        Font font = FontManager.small;

        fen = StrUtils.isEmpty(fen) ? Board.startingFen : fen;
        orientation = (orientation == null || orientation == PlayerColor.NO_PLAYER) ? PlayerColor.WHITE : orientation;
        iconSize = Size.min(iconSize);

        Size squareSize = new Size(iconSize);
        squareSize.multBy((double) 1 / 8);

        int textHeight = font.getSize();
        int textWidth = font.getSize() * fen.length();

        int w = Math.max(iconSize.width, textWidth);
        int h = iconSize.height + textHeight;

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        Size pieceSize = new Size(squareSize);
        pieceSize.multBy(0.8);

        Board board = new Board(fen);

        for (Square square : board) {
            Location loc = square.getLoc();
            if (orientation != PlayerColor.WHITE) {
                int r = Location.flip(loc.row);
                int c = Location.flip(loc.col);
                loc = Location.getLoc(r, c);
            }
            int x = loc.col * squareSize.width;
            int y = loc.row * squareSize.height;
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
        int x = 0;
        int y = squareSize.height * 8 - 1;
        g2d.setColor(Color.BLACK);
//            g2d.fillRect(x, y, textWidth, textHeight);

        g2d.setFont(font);
        g2d.drawString(fen, x, y + textHeight);

        return new ImageIcon(image);
    }

}
