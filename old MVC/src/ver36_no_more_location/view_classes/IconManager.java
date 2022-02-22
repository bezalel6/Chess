package ver36_no_more_location.view_classes;

import ver36_no_more_location.Player;
import ver36_no_more_location.model_classes.GameStatus;
import ver36_no_more_location.model_classes.pieces.Piece;
import ver36_no_more_location.model_classes.pieces.PieceType;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class IconManager {
    private ImageIcon[][] piecesIcons;
    private ImageIcon[][] gameOverIcons;

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

//    public static ImageIcon loadUnscaledPieceIcon(int player, int type) {
//        URL path = View.class.getResource("/Assets/" +  + ".png");
//        assert path != null;
//        ImageIcon ret = new ImageIcon(path);
//    }

    public ImageIcon loadImage(String relativePath) {
        URL path = getClass().getResource("/Assets/" + relativePath + ".png");
        assert path != null;
        ImageIcon ret = new ImageIcon(path);
        ret = scaleImage(ret);
        return ret;
    }

    public void loadAllIcons() {
        loadPiecesIcons();
        loadGameOverIcons();
    }

    private void loadGameOverIcons() {
        gameOverIcons = new ImageIcon[Player.NUM_OF_PLAYERS][3];
        for (Player player : Player.PLAYERS) {
            gameOverIcons[player.asInt()][0] = loadImage("GameOverIcons/Won/" + player.getName());
            gameOverIcons[player.asInt()][1] = loadImage("GameOverIcons/Lost/" + player.getName());
            gameOverIcons[player.asInt()][2] = loadImage("GameOverIcons/Tie/" + player.getName());
        }
    }

    public ImageIcon getGameOverIcon(GameStatus gameStatus, Player player) {
//        return gameOverIcons[player.asInt()][gameStatus.getGameStatusType()];
        return gameOverIcons[player.asInt()][0];
    }

    public ImageIcon getPieceIcon(Piece piece) {
        return getPieceIcon(piece.getPlayer(), piece.getPieceType());
    }

    public ImageIcon getPieceIcon(Player player, PieceType type) {
        return piecesIcons[player.asInt()][type.asInt()];
    }

    private void loadPiecesIcons() {
        piecesIcons = new ImageIcon[Player.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
        for (Player player : Player.PLAYERS) {
            for (PieceType pieceType : PieceType.PIECE_TYPES) {
                piecesIcons[player.asInt()][pieceType.asInt()] = loadImage(player.getName() + "/" + pieceType.getPieceName());
            }
        }
    }
}
