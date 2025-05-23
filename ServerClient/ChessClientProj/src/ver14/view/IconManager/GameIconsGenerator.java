package ver14.view.IconManager;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Square;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Utils.RegEx;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Board.BoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Game icons generator - utility class for generating icons of positions.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class GameIconsGenerator {

    /**
     * source: https://chess.stackexchange.com/questions/30004/longest-possible-fen#:~:text=Using%20the%20definition,4%2B5%20%3D%2090
     */
    private final static int maxFenLen = 90;

//    /**
//     * The entry point of application.
//     *
//     * @param args the input arguments
//     */
//    public static void main(String[] args) {
//        new JFrame() {{
//            setSize(500, 500);
//            add(new JLabel(GameIconsGenerator.generate("rnbqkbnr/pppp1ppp/8/8/4Pp2/8/PPPP2PP/RNBQKBNR w KQkq - 0 3", PlayerColor.BLACK, new Size(400, 400))));
//            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            setVisible(true);
//        }};
//    }

    /**
     * Generate icon for the given position fen, from the orientation of the given color.
     *
     * @param fen         the fen
     * @param orientation the orientation
     * @param iconSize    the icon size
     * @return the image icon
     */
    public static ImageIcon generate(String fen, PlayerColor orientation, Dimension iconSize) {
        if (!RegEx.Fen.check(fen)) {
            return IconManager.scaleImage(IconManager.redX, iconSize);
        }
        fen = StrUtils.isEmpty(fen) ? Board.startingFen : fen;
        orientation = (orientation == null || orientation == PlayerColor.NO_PLAYER) ? PlayerColor.WHITE : orientation;
        iconSize = Size.minSquare(iconSize);

        Size squareSize = new Size(iconSize);
        squareSize.multMe((double) 1 / 8);

//        Font font = FontManager.small;
//        int textHeight = font.getSize();
//        int textWidth = font.getSize() * maxFenLen;

        int w = iconSize.width;
        int h = iconSize.height;
//        int w = Math.max(iconSize.width, textWidth);
//        int h = iconSize.height + textHeight;

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        Size pieceSize = new Size(squareSize);
        pieceSize.multMe(0.8);

        Board board = new Board(fen);

        for (Square square : board) {
            Location loc = square.loc;
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

//        int x = 0;
//        int y = squareSize.height * 8 - 1;
//        g2d.setColor(Color.BLACK);
//
//        g2d.setFont(font);
//        g2d.drawString(fen, x, y + textHeight);

        return new ImageIcon(image);
    }

}
