package ver10.view.Dialog.Selectables;

import ver10.SharedClasses.PlayerColor;
import ver10.SharedClasses.SavedGames.GameInfo;
import ver10.SharedClasses.Sync.SyncableItem;
import ver10.view.IconManager.GameIconsGenerator;
import ver10.view.IconManager.Size;

import javax.swing.*;
import java.util.Objects;

public class Game implements Selectable, SyncableItem {
    private final GameInfo gameInfo;
    private final String txt;
    private ImageIcon icon = null;

    public Game(GameInfo gameInfo, Size iconSize) {
        this.gameInfo = gameInfo;
        PlayerColor startingClr = gameInfo.gameSettings.getPlayerToMove();
        PlayerColor joiningPlayerColor = startingClr.getOpponent();//no player's opp is no player
        txt = gameInfo.getGameDesc();
        if (iconSize != null)
            icon = GameIconsGenerator.generate(gameInfo.gameSettings.getFen(), joiningPlayerColor, iconSize);

    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game that = (Game) o;
        return Objects.equals(gameInfo, that.gameInfo) && Objects.equals(txt, that.txt);
    }

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public String getText() {
        return txt;
    }

    @Override
    public String ID() {
        return gameInfo.ID();
    }
}
