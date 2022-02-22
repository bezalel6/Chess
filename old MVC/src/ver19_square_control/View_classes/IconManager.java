package ver19_square_control.View_classes;

import ver19_square_control.GameStatus;

import javax.swing.*;
import java.awt.*;

import static ver19_square_control.types.Piece.*;

public class IconManager {
    private ImageIcon[][] piecesIcons;
    private ImageIcon[][] gameOverIcons;

    public void loadAllIcons() {
        loadPiecesIcons();
        loadGameOverIcons();
    }

    private void loadGameOverIcons() {
        gameOverIcons = new ImageIcon[NUM_OF_PLAYERS][3];
        for (int j = 0; j < PLAYER_NAMES.length; j++) {
            String clr = PLAYER_NAMES[j];
            gameOverIcons[j][GameStatus.WINNING_SIDE] = loadImage("GameOverIcons/Won/" + clr);
            gameOverIcons[j][GameStatus.LOSING_SIDE] = loadImage("GameOverIcons/Lost/" + clr);
            gameOverIcons[j][GameStatus.SIDE_NOT_RELEVANT] = loadImage("GameOverIcons/Tie/" + clr);
        }
    }

    public ImageIcon getGameOverIcon(GameStatus gameStatus, int player) {
        return gameOverIcons[player][gameStatus.getSide()];
    }

    public ImageIcon getPieceIcon(int player, int type) {
        return piecesIcons[player][type];
    }

    private void loadPiecesIcons() {
        piecesIcons = new ImageIcon[NUM_OF_PLAYERS][NUM_OF_PIECE_TYPES];
        for (int j = 0; j < PLAYER_NAMES.length; j++) {
            String clr = PLAYER_NAMES[j];
            for (int i = 0; i < PIECES_NAMES.length; i++) {
                String name = PIECES_NAMES[i];
                piecesIcons[j][i] = loadImage(clr + "/" + name);
            }
        }
    }

    private ImageIcon loadImage(String relativePath) {
        try {
            ImageIcon ret = new ImageIcon(View.class.getResource("/Assets/" + relativePath + ".png"));
            ret = scaleImage(ret);
            return ret;
        } catch (Exception e) {
            System.out.println("Couldnt load icon " + e);
            return null;
        }
    }

    public ImageIcon scaleImage(ImageIcon img) {
        return new ImageIcon(img.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
    }
}
