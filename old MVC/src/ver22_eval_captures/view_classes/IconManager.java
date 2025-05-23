package ver22_eval_captures.view_classes;

import ver22_eval_captures.Error;
import ver22_eval_captures.model_classes.GameStatus;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static ver22_eval_captures.types.Piece.*;
import static ver22_eval_captures.types.Piece.Player.PLAYER_NAMES;

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
        URL path = View.class.getResource("/Assets/" + relativePath + ".png");
        if (path == null)
            Error.error("couldnt load icon " + relativePath);
        else {
            ImageIcon ret = new ImageIcon(path);
            ret = scaleImage(ret);
            return ret;
        }
        return null;
    }

    public ImageIcon scaleImage(ImageIcon img) {
        return new ImageIcon(img.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
    }
}
