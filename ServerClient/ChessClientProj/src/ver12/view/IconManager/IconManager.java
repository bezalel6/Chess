package ver12.view.IconManager;


import ver12.SharedClasses.DBActions.Graphable.GraphElementType;
import ver12.SharedClasses.PlayerColor;
import ver12.SharedClasses.RegEx;
import ver12.SharedClasses.evaluation.GameStatus;
import ver12.SharedClasses.pieces.Piece;
import ver12.SharedClasses.pieces.PieceType;
import ver12.view.Board.BoardOverlay;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class IconManager {

    public static final ImageIcon promotionIcon;
    public static final DynamicIcon dynamicSettingsIcon;
    public static final DynamicIcon dynamicStatisticsIcon;
    public static final ImageIcon capturingIcon;
    public static final ImageIcon loadingIcon;
    public static final ImageIcon loginIcon;
    public static final ImageIcon userIcon;
    public static final ImageIcon registerIcon;
    public static final ImageIcon randomColorIcon;
    public static final ImageIcon greenCheck;
    public static final ImageIcon redX;
    //    public static final ImageIcon statisticsIcon;
    public static final ImageIcon[] graphIcons;
    public final static Size SECONDARY_COMP_SIZE = new Size(30);
    public final static Size LOGIN_PROCESS_SIZES = new Size(150);
    public final static Size USER_ICON_SIZE = new Size(25);
    public final static Size ABOVE_BTNS_SIZES = new Size(10);
    public static final ImageIcon showPassword;
    public static final ImageIcon hidePassword;
    private static final ImageIcon[][] gameOverIcons;
    private final static ImageIcon[][] piecesIcons;
    private static final int WON = 0;
    private static final int LOST = 1;
    private static final int TIE = 2;

    static {
        piecesIcons = new ImageIcon[PlayerColor.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
        gameOverIcons = new ImageIcon[PlayerColor.NUM_OF_PLAYERS][3];

        for (PlayerColor player : PlayerColor.PLAYER_COLORS) {

            for (PieceType pieceType : PieceType.PIECE_TYPES) {
                piecesIcons[player.asInt()][pieceType.asInt()] = loadImage(player.getName() + "/" + pieceType.getPieceName());
            }

            gameOverIcons[player.asInt()][WON] = loadImage("GameOverIcons/Won/" + player.getName());
            gameOverIcons[player.asInt()][LOST] = loadImage("GameOverIcons/Lost/" + player.getName());
            gameOverIcons[player.asInt()][TIE] = loadImage("GameOverIcons/Tie/" + player.getName());
        }
        hidePassword = loadImage("hidePassword");
        showPassword = loadImage("showPassword");

        promotionIcon = loadImage("promotion", ABOVE_BTNS_SIZES);
        capturingIcon = loadImage("redX", ABOVE_BTNS_SIZES);

        randomColorIcon = loadImage("random");

        userIcon = loadImage("user", USER_ICON_SIZE);

        loginIcon = loadImage("login", LOGIN_PROCESS_SIZES);
        registerIcon = loadImage("register", LOGIN_PROCESS_SIZES);

        loadingIcon = loadGif("loading", SECONDARY_COMP_SIZE);
        greenCheck = loadImage("greenCheck", SECONDARY_COMP_SIZE);
        redX = loadImage("redX", SECONDARY_COMP_SIZE);

        dynamicSettingsIcon = new DynamicIcon("SettingsIcon/", SECONDARY_COMP_SIZE);

        dynamicStatisticsIcon = new DynamicIcon("Statistics/StatisticsIcon/", SECONDARY_COMP_SIZE);

//        statisticsIcon = loadImage("statistics", SECONDARY_COMP_SIZE);
        graphIcons = new ImageIcon[GraphElementType.values().length];
        for (GraphElementType graphElementType : GraphElementType.values()) {
            graphIcons[graphElementType.ordinal()] = loadImage("Statistics/" + graphElementType.iconName(), new Size(25, 1));
        }
    }

    public static ImageIcon getStatisticsIcon(GraphElementType graphElementType) {
        return graphIcons[graphElementType.ordinal()];
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

    public static void main(String[] args) {
        new JFrame() {{
            setSize(500, 500);
            add(new JLabel(getPieceIcon(Piece.B_N)));

            setVisible(true);
        }};
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

    public static ImageIcon screenshot(Component comp, Dimension... size) {
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

    public static ImageIcon scaleImage(ImageIcon img, Dimension... _size) {
        Size size = Size.size(_size);
        return new ImageIcon(img.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH), img.getDescription());
    }

    public static ImageIcon screenshot(BoardOverlay boardOverlay, Dimension... size) {
        return screenshot(boardOverlay, Size.size(size));
    }

    public static ImageIcon screenshot(BoardOverlay boardOverlay, Size... size) {
        Component comp = boardOverlay.getJlayer();
        int h = Math.max(100, comp.getHeight());
        int w = Math.max(100, comp.getWidth());
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        comp.paint(graphics2D);
        ImageIcon ret = new ImageIcon(image);
        if (size.length == 0) {
            return ret;
        }
        return scaleImage(ret, size);
    }

    /**
     * @param relativePath file extension is required!
     * @param size
     * @return
     */
    public static ImageIcon load(String relativePath, Size... size) {
        assert RegEx.Icon.check(relativePath);
        return relativePath.endsWith(".png") ? loadImage(relativePath, size) : loadGif(relativePath, size);
    }

    /**
     * size: [width,height]
     *
     * @param relativePath
     * @param _size
     * @return
     */
    public static MyIcon loadImage(String relativePath, Size... _size) {
        ImageIcon ret = loadNoScale(relativePath);
        ret = scaleImage(ret, _size);
        return new MyIcon(ret);
    }

    public static ImageIcon loadGif(String relativePath, Dimension... _size) {
        if (!relativePath.endsWith(".gif")) {
            relativePath += ".gif";
        }
        Size size = Size.size(_size);
        ImageIcon icon = loadNoScale(relativePath);
        icon.setImage(icon.getImage().getScaledInstance(size.width, size.height, Image.SCALE_DEFAULT));
        return icon;
    }

    public static ImageIcon loadNoScale(String relativePath) {
        if (!RegEx.Icon.check(relativePath)) {
            relativePath += ".png";
        }
        relativePath = "/assets/" + relativePath;
        URL path = IconManager.class.getResource(relativePath);
        assert path != null;
        return new ImageIcon(path, path.getPath());
    }

    public static class MyIcon extends ImageIcon {

        public MyIcon(ImageIcon image) {
            super(image.getImage());
        }
    }


}
