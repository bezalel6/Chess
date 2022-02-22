package ver7.view;


import ver7.SharedClasses.PlayerColor;
import ver7.SharedClasses.RegEx;
import ver7.SharedClasses.evaluation.GameStatus;
import ver7.SharedClasses.pieces.Piece;
import ver7.SharedClasses.pieces.PieceType;
import ver7.view.Graph.GraphElementType;
import ver7.view.board.BoardOverlay;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class IconManager {
    public static final ImageIcon promotionIcon;
    public static final ImageIcon capturingIcon;
    public static final ImageIcon loadingIcon;
    public static final ImageIcon loginIcon;
    public static final ImageIcon registerIcon;
    public static final ImageIcon randomColorIcon;
    public static final ImageIcon greenCheck;
    public static final ImageIcon redX;
    public static final ImageIcon statisticsIcon;
    public static final ImageIcon[] statisticsIcons;
    public final static Size SECONDARY_COMP_SIZE = new Size(20);
    public final static Size LOGIN_PROCESS_SIZES = new Size(150);
    public final static Size ABOVE_BTNS_SIZES = new Size(10);
    public static final ImageIcon showPassword = loadImage("showPassword");
    public static final ImageIcon hidePassword = loadImage("hidePassword");
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
        promotionIcon = loadImage("promotion", ABOVE_BTNS_SIZES);
        capturingIcon = loadImage("redX", ABOVE_BTNS_SIZES);

        randomColorIcon = loadImage("random");

        loginIcon = loadImage("login", LOGIN_PROCESS_SIZES);
        registerIcon = loadImage("register", LOGIN_PROCESS_SIZES);

        loadingIcon = loadGif("loading", SECONDARY_COMP_SIZE);
        greenCheck = loadImage("greenCheck", SECONDARY_COMP_SIZE);
        redX = loadImage("redX", SECONDARY_COMP_SIZE);

        statisticsIcon = loadImage("statistics", SECONDARY_COMP_SIZE);
        statisticsIcons = new ImageIcon[GraphElementType.values().length];
        for (GraphElementType graphElementType : GraphElementType.values()) {
            statisticsIcons[graphElementType.ordinal()] = loadImage("Statistics/" + graphElementType.iconName(), new Size(25, 1));
        }
    }

    public static ImageIcon getStatisticsIcon(GraphElementType graphElementType) {
        return statisticsIcons[graphElementType.ordinal()];
    }

    /**
     * size: [width,height]
     *
     * @param relativePath
     * @param _size
     * @return
     */
    public static ImageIcon loadImage(String relativePath, Size... _size) {
        ImageIcon ret = loadNoScale(relativePath);
        ret = scaleImage(ret, _size);
        return ret;
    }

    public static ImageIcon loadNoScale(String relativePath) {
        if (!RegEx.Icon.check(relativePath)) {
            relativePath += ".png";
        }
        URL path = IconManager.class.getResource("/assets/" + relativePath);
        assert path != null;
        return new ImageIcon(path);
    }

    public static ImageIcon scaleImage(ImageIcon img, Size... _size) {
        Size size = Size.size(_size);
        return new ImageIcon(img.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH));
    }

    public static ImageIcon loadGif(String relativePath, Size... _size) {
        if (!relativePath.endsWith(".gif")) {
            relativePath += ".gif";
        }
        Size size = Size.size(_size);
        ImageIcon icon = loadNoScale(relativePath);
        icon.setImage(icon.getImage().getScaledInstance(size.width, size.height, Image.SCALE_DEFAULT));
        return icon;
    }

    public static ImageIcon copyImage(Icon og) {
        return copyImage((ImageIcon) og);
    }

    public static ImageIcon copyImage(ImageIcon og) {
        return new ImageIcon(og.getImage());
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
        if (piece == null) return null;
        return getPieceIcon(piece.getPlayer(), piece.getPieceType());
    }

    public static ImageIcon getPieceIcon(PlayerColor player, PieceType type) {
        return piecesIcons[player.asInt()][type.asInt()];
    }

    public static ImageIcon getPlayerIcon(PlayerColor playerColor) {
        if (playerColor == PlayerColor.NO_PLAYER)
            return randomColorIcon;
        return getPieceIcon(playerColor, PieceType.KING);
    }

    public static ImageIcon screenshot(Component comp, Size... size) {
        try {
            BufferedImage image = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            comp.repaint();
            comp.paint(graphics2D);
            ImageIcon ret = new ImageIcon(image);
            if (size.length == 0) {
                return ret;
            }
            return scaleImage(ret, size);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ImageIcon screenshot(BoardOverlay boardOverlay, Size... size) {
        Component comp = boardOverlay.getJlayer();
        BufferedImage image = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        comp.paint(graphics2D);
        ImageIcon ret = new ImageIcon(image);
        if (size.length == 0) {
            return ret;
        }
        return scaleImage(ret, size);
    }


    public static class Size extends Dimension {
        public Size() {
            this(80);
        }

        public Size(int size) {
            this(size, size);
        }

        public Size(int width, int height) {
            super(width, height);
        }

        public Size(Size size) {
            this(size.width, size.height);
        }

        public Size(Dimension size) {
            this(size.width, size.height);
        }

        public static Size size(Size... _size) {
            return _size.length == 0 ? new Size() : _size[0];
        }

        public Size padding(Insets insets) {
            return new Size(width - (insets.left + insets.right), height - (insets.top + insets.bottom));
        }

        public void multBy(double mult) {
            width *= mult;
            height *= mult;
        }

        public int maxDiff(Dimension size) {
            return Math.max(Math.abs(width - size.width), Math.abs(height - size.height));
        }
    }


}
