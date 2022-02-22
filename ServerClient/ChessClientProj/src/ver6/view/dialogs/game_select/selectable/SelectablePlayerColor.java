package ver6.view.dialogs.game_select.selectable;

import ver6.SharedClasses.PlayerColor;
import ver6.view.IconManager;
import ver6.view.dialogs.Selectable;

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
