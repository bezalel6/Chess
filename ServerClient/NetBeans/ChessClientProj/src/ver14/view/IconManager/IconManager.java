package ver14.view.IconManager;


import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Misc.Environment;
import ver14.SharedClasses.Utils.RegEx;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Board.BoardOverlay;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class IconManager {

    public static final DynamicIcon dynamicSettingsIcon;
    public static final DynamicIcon dynamicStatisticsIcon;
    public static final DynamicIcon dynamicServerIcon;

    public static final ImageIcon promotionIcon;
    public static final ImageIcon capturingIcon;
    public static final ImageIcon loadingIcon;
    public static final ImageIcon loginIcon;
    public static final ImageIcon userIcon;
    public static final ImageIcon defaultUserIcon;
    public static final ImageIcon registerIcon;
    public static final ImageIcon randomColorIcon;
    public static final ImageIcon greenCheck;
    public static final ImageIcon redX;
    public static final ImageIcon showPassword;
    public static final ImageIcon hidePassword;
    public final static Size SECONDARY_COMP_SIZE = new Size(30);
    public final static Size PROFILE_PIC_SIZE = new Size(25);
    public final static Size PROFILE_PIC_BIG_SIZE = new Size(50);

    public final static Size LOGIN_PROCESS_SIZES = new Size(150);
    public final static Size ABOVE_BTNS_SIZES = new Size(10);
    public final static Size MESSAGES_ICONS = new Size(20);
    public static final ImageIcon infoIcon;
    public static final ImageIcon errorIcon;
    public static final Size OG_SIZE = new Size(-1);
    public static final ImageIcon serverError;
    private static final ImageIcon[][] gameOverIcons;
    private final static ImageIcon[][] piecesIcons;
    private static final int WON = 0;
    private static final int LOST = 1;
    private static final int TIE = 2;

    //todo vars for path
    static {
        piecesIcons = new ImageIcon[PlayerColor.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
        gameOverIcons = new ImageIcon[PlayerColor.NUM_OF_PLAYERS][3];

        for (PlayerColor player : PlayerColor.PLAYER_COLORS) {

            for (PieceType pieceType : PieceType.PIECE_TYPES) {
                piecesIcons[player.asInt][pieceType.asInt] = loadImage(player.getName() + "/" + pieceType.getPieceName());
            }

            gameOverIcons[player.asInt][WON] = loadImage("GameOverIcons/Won/" + player.getName());
            gameOverIcons[player.asInt][LOST] = loadImage("GameOverIcons/Lost/" + player.getName());
            gameOverIcons[player.asInt][TIE] = loadImage("GameOverIcons/Tie/" + player.getName());
        }

        serverError = loadImage("StatusIcons/serverError", MESSAGES_ICONS);

        infoIcon = loadImage("StatusIcons/Info", MESSAGES_ICONS);
        errorIcon = loadImage("StatusIcons/Error", MESSAGES_ICONS);

        hidePassword = loadImage("hidePassword");
        showPassword = loadImage("showPassword");

        promotionIcon = loadImage("promotion", ABOVE_BTNS_SIZES);
        capturingIcon = loadImage("redX", ABOVE_BTNS_SIZES);

        randomColorIcon = loadImage("random");

        userIcon = loadImage("user", PROFILE_PIC_SIZE);
        defaultUserIcon = userIcon;

        loginIcon = loadImage("login", LOGIN_PROCESS_SIZES);
        registerIcon = loadImage("register", LOGIN_PROCESS_SIZES);

        loadingIcon = loadGif("loading", SECONDARY_COMP_SIZE);
        greenCheck = loadImage("greenCheck", SECONDARY_COMP_SIZE);
        redX = loadImage("redX", SECONDARY_COMP_SIZE);

        dynamicSettingsIcon = new DynamicIcon("SettingsIcon/", SECONDARY_COMP_SIZE);

        dynamicStatisticsIcon = new DynamicIcon("Statistics/StatisticsIcon/", SECONDARY_COMP_SIZE);

        dynamicServerIcon = new DynamicIcon("/ServerIcon/", SECONDARY_COMP_SIZE);

    }

    public static ImageIcon copyImage(Icon og) {
        return copyImage((ImageIcon) og);
    }

    public static ImageIcon copyImage(ImageIcon og) {
        return new ImageIcon(og.getImage(), og.getDescription());
    }

    public static ImageIcon getGameOverIcon(GameStatus gameStatus, PlayerColor player) {
        int index = TIE;
        if (player == gameStatus.getWinningColor()) {
            index = WON;
        } else if (player.getOpponent() == gameStatus.getWinningColor()) {
            index = LOST;
        }
        return gameOverIcons[player.asInt][index];
    }

    public static void main(String[] args) {
        new JFrame() {{
            setSize(500, 500);
            add(new JLabel(loadOnline("https://cdn.tech.eu/uploads/2022/02/jan-meeus-7LsuYqkvIUM-unsplash-scaled.jpg")));

            setVisible(true);
        }};
    }

    public static MyIcon loadOnline(String relativePath, Size... _size) {
        URL url = null;
        try {
            url = new URL(relativePath);
        } catch (MalformedURLException e) {
            return null;
        }
        ImageIcon ret = loadNoScale(url);
        if (ret == null)
            return null;
        ret = scaleImage(ret, _size);
        return new MyIcon(ret);
    }

    public static ImageIcon loadNoScale(URL url) {
        ImageIcon icon = new ImageIcon(url, url.toString());
//        new JFrame() {{
//            setSize(500, 500);
//            setIconImage(icon.getImage());
//            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            setVisible(true);
//        }};
        return icon.getIconWidth() == -1 ? null : icon;
    }

    public static ImageIcon scaleImage(ImageIcon img, Dimension... _size) {
        Size size = Size.size(_size);
        if (size instanceof Size.RatioSize)
            size.keepRatio(new Size(img.getIconWidth(), img.getIconHeight()));

        if (size == OG_SIZE)
            return img;
        ImageIcon noScale;
        try {
            noScale = loadNoScale(img.getDescription());
            assert noScale != null && noScale.getIconWidth() != 0;
            img = noScale;
        } catch (Exception | AssertionError e) {
        }

        return new ImageIcon(img.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH), img.getDescription());
    }

    public static ImageIcon loadNoScale(String relativePath) {
        relativePath = StrUtils.clean(relativePath);
        boolean isNotComplete = !StrUtils.isAbsoluteUrl(relativePath);
        if (isNotComplete && !RegEx.Icon.check(relativePath)) {
            relativePath += ".png";
        }
        if (isNotComplete && !relativePath.contains("/assets/"))
            relativePath = "/assets/" + relativePath;
        URL path;
        if (Environment.IS_JAR) {
            try {
                if (isNotComplete && !relativePath.contains("./")) {
                    relativePath = "./" + relativePath;
                }
                if (!isNotComplete && relativePath.contains("./")) {
                    relativePath = relativePath.replaceAll("\\./", "");
                }
                relativePath = relativePath.replaceFirst("file:/", "");
                if (isNotComplete) {
                    File file = new File(relativePath);
                    if (!file.exists())
                        System.out.println("didnt find file " + relativePath);
                    path = file.toURI().toURL();

                } else {
                    path = new URL(relativePath);
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else

            path = IconManager.class.getResource(relativePath);
        assert path != null;
        return loadNoScale(path);
    }

    public static MyIcon loadImage(String relativePath, Size... _size) {
        ImageIcon ret = loadNoScale(relativePath);
        ret = scaleImage(ret, _size);
        return new MyIcon(ret);
    }

    public static ImageIcon getPieceIcon(Piece piece) {
        if (piece == null) return null;
        return getPieceIcon(piece.playerColor, piece.pieceType);
    }

    public static ImageIcon getPieceIcon(PlayerColor player, PieceType type) {
        return piecesIcons[player.asInt][type.asInt];
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

    public static ImageIcon loadGif(String relativePath, Dimension... _size) {
        if (!relativePath.endsWith(".gif")) {
            relativePath += ".gif";
        }
        Size size = Size.size(_size);
        ImageIcon icon = loadNoScale(relativePath);
        icon.setImage(icon.getImage().getScaledInstance(size.width, size.height, Image.SCALE_DEFAULT));
        return icon;
    }

    public static ImageIcon loadUserIcon(String url) {
        return loadUserIcon(url, PROFILE_PIC_SIZE);
    }

    public static ImageIcon loadUserIcon(String url, Size size) {
        size = Size.size(size);
        size = new Size.RatioSize(size);
        if (StrUtils.isEmpty(url)) {
            return scaleImage(defaultUserIcon, size);
        }
        return loadOnline(url, size);
    }

    public static class MyIcon extends ImageIcon {

        public MyIcon(ImageIcon image) {
            super(image.getImage(), image.getDescription());
        }
    }


}
