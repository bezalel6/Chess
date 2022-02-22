package ver6.view.dialogs.game_select.selectable;

import ver6.SharedClasses.PlayerColor;
import ver6.SharedClasses.SavedGames.GameInfo;
import ver6.view.GameIconsGenerator;
import ver6.view.IconManager;
import ver6.view.dialogs.Selectable;

import javax.swing.*;
import java.util.Objects;

public class SelectableGame implements Selectable {
    public final GameInfo gameInfo;
    private final String txt;
    private ImageIcon icon = null;

    public SelectableGame(GameInfo gameInfo, IconManager.Size iconSize) {
        this.gameInfo = gameInfo;
        PlayerColor startingClr = gameInfo.gameSettings.getPlayerToMove();
        PlayerColor joiningPlayerColor = startingClr.getOpponent();//no player's opp is itself
        txt = "%s %s (%s)".formatted(gameInfo.creatorUsername, joiningPlayerColor == PlayerColor.NO_PLAYER ? "Random" : joiningPlayerColor, gameInfo.gameId);
        if (gameInfo.gameSettings.getFen() != null)
            icon = GameIconsGenerator.generate(gameInfo.gameSettings.getFen(), joiningPlayerColor, iconSize);

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
}
