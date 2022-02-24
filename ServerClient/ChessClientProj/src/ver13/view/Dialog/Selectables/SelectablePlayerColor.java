package ver13.view.Dialog.Selectables;

import ver13.SharedClasses.PlayerColor;
import ver13.view.IconManager.IconManager;

import javax.swing.*;

public enum SelectablePlayerColor implements Selectable {
    WHITE(PlayerColor.WHITE),
    RANDOM(PlayerColor.NO_PLAYER),
    BLACK(PlayerColor.BLACK);

    public final PlayerColor playerColor;
    private final ImageIcon icon;

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
