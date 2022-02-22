package ver8.view.OldDialogs.GameSelect.selectable;

import ver8.SharedClasses.PlayerColor;
import ver8.view.IconManager;
import ver8.view.OldDialogs.Selectable;

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
