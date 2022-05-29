package ver14.view.Dialog.Selectables;

import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.SavedGames.GameInfo;
import ver14.SharedClasses.Sync.SyncableItem;
import ver14.view.IconManager.GameIconsGenerator;
import ver14.view.IconManager.Size;

import javax.swing.*;
import java.util.Objects;

/**
 * a selectable Game.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class SelectableGame implements Selectable, SyncableItem {
    /**
     * The Game info.
     */
    private final GameInfo gameInfo;
    /**
     * The Txt.
     */
    private final String txt;
    /**
     * The Icon.
     */
    private ImageIcon icon = null;

    /**
     * Instantiates a new Game.
     *
     * @param gameInfo the game info
     * @param iconSize the icon size
     */
    public SelectableGame(GameInfo gameInfo, Size iconSize) {
        this.gameInfo = gameInfo;
        PlayerColor startingClr = gameInfo.gameSettings.getPlayerToMove();
//        PlayerColor joiningPlayerColor = startingClr.getOpponent();//no player's opp is no player
        txt = gameInfo.getGameDesc();
        if (iconSize != null)
            icon = GameIconsGenerator.generate(gameInfo.gameSettings.getFen(), PlayerColor.WHITE, iconSize);

    }

    /**
     * Gets game info.
     *
     * @return the game info
     */
    public GameInfo getGameInfo() {
        return gameInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectableGame that = (SelectableGame) o;
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
