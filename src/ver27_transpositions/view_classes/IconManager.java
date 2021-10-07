package ver27_transpositions.view_classes;

import ver27_transpositions.model_classes.GameStatus;
import ver27_transpositions.model_classes.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static ver27_transpositions.Player.PLAYER_NAMES;
import static ver27_transpositions.model_classes.pieces.Piece.*;

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
        gameOverIcons = new ImageIcon[NUM_OF_PLAYERS][3];
        for (int j = 0; j < NUM_OF_PLAYERS; j++) {
            String clr = PLAYER_NAMES[j];
            gameOverIcons[j][0] = loadImage("GameOverIcons/Won/" + clr);
            gameOverIcons[j][1] = loadImage("GameOverIcons/Lost/" + clr);
            gameOverIcons[j][2] = loadImage("GameOverIcons/Tie/" + clr);
        }
    }

    public ImageIcon getGameOverIcon(GameStatus gameStatus, int player) {
//        return gameOverIcons[player][gameStatus.getGameStatusType()];
        return gameOverIcons[player][0];
    }

    public ImageIcon getPieceIcon(Piece piece) {
        return getPieceIcon(piece.getPieceColor(), piece.getPieceType());
    }

    public ImageIcon getPieceIcon(int player, int type) {
        return piecesIcons[player][type];
    }

    private void loadPiecesIcons() {
        piecesIcons = new ImageIcon[NUM_OF_PLAYERS][NUM_OF_PIECE_TYPES];
        for (int j = 0; j < NUM_OF_PLAYERS; j++) {
            String clr = PLAYER_NAMES[j];
            for (int i = 0; i < NUM_OF_PIECE_TYPES; i++) {
                String name = PIECES_NAMES[i];
                piecesIcons[j][i] = loadImage(clr + "/" + name);
            }
        }
    }
}
