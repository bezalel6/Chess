package ver14.view.Dialog.Selectables;

import ver14.SharedClasses.Game.PlayerColor;
import ver14.view.IconManager.IconManager;

import javax.swing.*;

/**
 * represents a Selectable player color.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum SelectablePlayerColor implements Selectable {
    /**
     * White selectable player color.
     */
    WHITE(PlayerColor.WHITE),
    /**
     * Random selectable player color.
     */
    RANDOM(PlayerColor.NO_PLAYER),
    /**
     * Black selectable player color.
     */
    BLACK(PlayerColor.BLACK);

    /**
     * The Player color.
     */
    public final PlayerColor playerColor;
    /**
     * The Icon.
     */
    private final ImageIcon icon;

    /**
     * Instantiates a new Selectable player color.
     *
     * @param playerColor the player color
     */
    SelectablePlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
        icon = IconManager.getPlayerIcon(playerColor);
    }

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public String getText() {
        return "";
    }
}
