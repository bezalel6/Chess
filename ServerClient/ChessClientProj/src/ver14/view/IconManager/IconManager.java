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

/**
 * Icon manager - utility class for loading icons.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class IconManager {

    /**
     * The constant dynamicSettingsIcon.
     */
    public static final DynamicIcon dynamicSettingsIcon;
    /**
     * The constant dynamicStatisticsIcon.
     */
    public static final DynamicIcon dynamicStatisticsIcon;
    /**
     * The constant dynamicServerIcon.
     */
    public static final DynamicIcon dynamicServerIcon;

    /**
     * The constant promotionIcon.
     */
    public static final MyImage promotionIcon;
    /**
     * The constant capturingIcon.
     */
    public static final MyImage capturingIcon;
    /**
     * The constant loadingIcon.
     */
    public static final MyImage loadingIcon;
    /**
     * The constant loginIcon.
     */
    public static final MyImage loginIcon;
    /**
     * The constant userIcon.
     */
    public static final MyImage userIcon;
    /**
     * The constant defaultUserIcon.
     */
    public static final MyImage defaultUserIcon;
    /**
     * The constant registerIcon.
     */
    public static final MyImage registerIcon;
    /**
     * The constant randomColorIcon.
     */
    public static final MyImage randomColorIcon;
    /**
     * The constant greenCheck.
     */
    public static final MyImage greenCheck;
    /**
     * The constant redX.
     */
    public static final MyImage redX;
    /**
     * The constant showPassword.
     */
    public static final MyImage showPassword;
    /**
     * The constant hidePassword.
     */
    public static final MyImage hidePassword;
    /**
     * The constant SECONDARY_COMP_SIZE.
     */
    public final static Size SECONDARY_COMP_SIZE = new Size(30);
    /**
     * The constant PROFILE_PIC_SIZE.
     */
    public final static Size PROFILE_PIC_SIZE = new Size(25);
    /**
     * The constant PROFILE_PIC_BIG_SIZE.
     */
    public final static Size PROFILE_PIC_BIG_SIZE = new Size(50);

    /**
     * The constant LOGIN_PROCESS_SIZES.
     */
    public final static Size LOGIN_PROCESS_SIZES = new Size(150);
    /**
     * The constant ABOVE_BTNS_SIZES.
     */
    public final static Size ABOVE_BTNS_SIZES = new Size(10);
    /**
     * The constant MESSAGES_ICONS.
     */
    public final static Size MESSAGES_ICONS = new Size(20);
    /**
     * The constant infoIcon.
     */
    public static final MyImage infoIcon;
    /**
     * The constant errorIcon.
     */
    public static final MyImage errorIcon;
    /**
     * The constant OG_SIZE.
     */
    public static final Size OG_SIZE = new Size(-1);
    /**
     * The constant serverError.
     */
    public static final MyImage serverError;
    /**
     * The constant gameOverIcons.
     */
    private static final MyImage[][] gameOverIcons;
    /**
     * The Pieces icons.
     */
    private final static MyImage[][] piecesIcons;
    /**
     * The constant WON.
     */
    private static final int WON = 0;
    /**
     * The constant LOST.
     */
    private static final int LOST = 1;
    /**
     * The constant TIE.
     */
    private static final int TIE = 2;

    //todo vars for path
    static {
        piecesIcons = new MyImage[PlayerColor.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES];
        gameOverIcons = new MyImage[PlayerColor.NUM_OF_PLAYERS][3];

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

    /**
     * Copy image.
     *
     * @param og the original image
     * @return the new copy
     */
    public static ImageIcon copyImage(Icon og) {
        return copyImage((ImageIcon) og);
    }

    /**
     * Copy image.
     *
     * @param og the original image
     * @return the new copy
     */
    public static ImageIcon copyImage(ImageIcon og) {
        return new ImageIcon(og.getImage(), og.getDescription());
    }

    /**
     * Gets game over icon.
     *
     * @param gameStatus the game status
     * @param player     the player
     * @return the game over icon
     */
    public static ImageIcon getGameOverIcon(GameStatus gameStatus, PlayerColor player) {
        int index = TIE;
        if (player == gameStatus.getWinningColor()) {
            index = WON;
        } else if (player.getOpponent() == gameStatus.getWinningColor()) {
            index = LOST;
        }
        return gameOverIcons[player.asInt][index];
    }


    /**
     * Load image from an online source.
     *
     * @param path the path to the image
     * @param _size        the optional size of the image
     * @return the loaded image if it loaded successfully. null otherwise
     */
    public static MyImage loadOnline(String path, Size... _size) {
        URL url = null;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            return null;
        }
        ImageIcon ret = loadNoScale(url);
        if (ret == null)
            return null;
        ret = scaleImage(ret, _size);
        return new MyImage(ret);
    }

    /**
     * Load an image without scaling it.
     *
     * @param url the url to the image
     * @return the loaded image if one is loaded successfully. null otherwise.
     */
    public static MyImage loadNoScale(URL url) {
        MyImage icon = new MyImage(url, url.toString());
//        new JFrame() {{
//            setSize(500, 500);
//            setIconImage(icon.getImage());
//            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            setVisible(true);
//        }};
        return icon.getIconWidth() == -1 ? null : icon;
    }

    /**
     * Scale an image.
     *
     * @param img   the image to scale
     * @param _size the optional size. if one is not passed, the image will scale to {@value  Size#DEFAULT_SIZE }
     * @return the image icon
     */
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

    /**
     * Load an image from a relative path.
     *
     * @param relativePath the relative path
     * @return the loaded image
     */
    public static MyImage loadNoScale(String relativePath) {
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
                    relativePath = "./ClientAssets/" + relativePath;
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
        } else{
            path = IconManager.class.getResource(relativePath);
        }
        assert path != null;
        return loadNoScale(path);
    }

    /**
     * Load an image from a relative path.
     *
     * @param relativePath the relative path
     * @param _size        the optional size
     * @return the loaded image
     */
    public static MyImage loadImage(String relativePath, Size... _size) {
        ImageIcon ret = loadNoScale(relativePath);
        ret = scaleImage(ret, _size);
        return new MyImage(ret);
    }

    /**
     * Gets a piece icon.
     *
     * @param piece the piece
     * @return the piece icon
     */
    public static ImageIcon getPieceIcon(Piece piece) {
        if (piece == null) return null;
        return getPieceIcon(piece.playerColor, piece.pieceType);
    }

    /**
     * Gets piece icon.
     *
     * @param player the player
     * @param type   the type
     * @return the piece icon
     */
    public static ImageIcon getPieceIcon(PlayerColor player, PieceType type) {
        return piecesIcons[player.asInt][type.asInt];
    }

    /**
     * Gets player icon.
     *
     * @param playerColor the player color
     * @return the player icon
     */
    public static ImageIcon getPlayerIcon(PlayerColor playerColor) {
        if (playerColor == PlayerColor.NO_PLAYER)
            return randomColorIcon;
        return getPieceIcon(playerColor, PieceType.KING);
    }

    /**
     * Screenshot image icon.
     *
     * @param comp the comp
     * @param size the size
     * @return the image icon
     */
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

    /**
     * Screenshot image icon.
     *
     * @param boardOverlay the board overlay
     * @param size         the size
     * @return the image icon
     */
    public static ImageIcon screenshot(BoardOverlay boardOverlay, Dimension... size) {
        return screenshot(boardOverlay, Size.size(size));
    }

    /**
     * Screenshot image icon.
     *
     * @param boardOverlay the board overlay
     * @param size         the size
     * @return the image icon
     */
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
     * Load image icon.
     *
     * @param relativePath file extension is required!
     * @param size         the size
     * @return image icon
     */
    public static ImageIcon load(String relativePath, Size... size) {
        assert RegEx.Icon.check(relativePath);
        return relativePath.endsWith(".png") ? loadImage(relativePath, size) : loadGif(relativePath, size);
    }

    /**
     * Load gif my icon.
     *
     * @param relativePath the relative path
     * @param _size        the size
     * @return the my icon
     */
    public static MyImage loadGif(String relativePath, Dimension... _size) {
        if (!relativePath.endsWith(".gif")) {
            relativePath += ".gif";
        }
        Size size = Size.size(_size);
        MyImage icon = loadNoScale(relativePath);
        icon.setImage(icon.getImage().getScaledInstance(size.width, size.height, Image.SCALE_DEFAULT));
        return icon;
    }

    /**
     * Load user icon image icon.
     *
     * @param url the url
     * @return the image icon
     */
    public static ImageIcon loadUserIcon(String url) {
        return loadUserIcon(url, PROFILE_PIC_SIZE);
    }

    /**
     * Load user icon image icon.
     *
     * @param url  the url
     * @param size the size
     * @return the image icon
     */
    public static ImageIcon loadUserIcon(String url, Size size) {
        size = Size.size(size);
        size = new Size.RatioSize(size);
        if (StrUtils.isEmpty(url)) {
            return scaleImage(defaultUserIcon, size);
        }
        return loadOnline(url, size);
    }


}
