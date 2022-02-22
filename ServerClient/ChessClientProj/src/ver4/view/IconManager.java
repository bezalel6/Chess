package ver4.view;


import ver4.SharedClasses.PlayerColor;
import ver4.SharedClasses.evaluation.GameStatus;
import ver4.SharedClasses.pieces.Piece;
import ver4.SharedClasses.pieces.PieceType;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class IconManager {
    private static final ImageIcon[][] gameOverIcons = new ImageIcon[PlayerColor.NUM_OF_PLAYERS][3];
    private final static ImageIcon[][] piecesIcons = new ImageIcon[PlayerColor.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
    private static final int WON = 0;
    private static final int LOST = 1;
    private static final int TIE = 2;

    static {
        for (PlayerColor player : PlayerColor.PLAYER_COLORS) {

            for (PieceType pieceType : PieceType.PIECE_TYPES) {
                piecesIcons[player.asInt()][pieceType.asInt()] = loadImage(player.getName() + "/" + pieceType.getPieceName());
            }

            gameOverIcons[player.asInt()][WON] = loadImage("GameOverIcons/Won/" + player.getName());
            gameOverIcons[player.asInt()][LOST] = loadImage("GameOverIcons/Lost/" + player.getName());
            gameOverIcons[player.asInt()][TIE] = loadImage("GameOverIcons/Tie/" + player.getName());
        }
    }

    public static ImageIcon scaleImage(ImageIcon img, int width, int height) {
        width = width == 0 ? 1 : width;
        height = height == 0 ? 1 : height;
        return new ImageIcon(img.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public static ImageIcon copyImage(Icon og) {
        return copyImage((ImageIcon) og);
    }

    public static ImageIcon copyImage(ImageIcon og) {
        return new ImageIcon(og.getImage());
    }

    public static ImageIcon scaleImage(ImageIcon img) {
        return scaleImage(img, 80, 80);
    }

    public static ImageIcon loadImage(String relativePath) {
        URL path = IconManager.class.getResource("/assets/" + relativePath + ".png");
        assert path != null;
        ImageIcon ret = new ImageIcon(path);
        ret = scaleImage(ret);
        return ret;
    }

    public static ImageIcon getGameOverIcon(GameStatus gameStatus, PlayerColor player) {
        int index = TIE;
        if (player == gameStatus.getWinningColor()) {
            index = WON;
        } else if (player.getOpponent() == gameStatus.getWinningColor()) {
            index = LOST;
        }
        return gameOverIcons[player.asInt()][index];
    }

    public static ImageIcon getPieceIcon(Piece piece) {
        return getPieceIcon(piece.getPlayer(), piece.getPieceType());
    }

    public static ImageIcon getPieceIcon(PlayerColor player, PieceType type) {
        return piecesIcons[player.asInt()][type.asInt()];
    }

}
