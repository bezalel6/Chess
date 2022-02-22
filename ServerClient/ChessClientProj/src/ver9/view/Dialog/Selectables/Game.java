package ver9.view.Dialog.Selectables;

import ver9.SharedClasses.PlayerColor;
import ver9.SharedClasses.SavedGames.GameInfo;
import ver9.view.GameIconsGenerator;
import ver9.view.IconManager;

import javax.swing.*;
import java.util.Objects;

public class Game implements Selectable {
    private final GameInfo gameInfo;
    private final String txt;
    private ImageIcon icon = null;
    public Game(GameInfo gameInfo, IconManager.Size iconSize) {
        this.gameInfo = gameInfo;
        PlayerColor startingClr = gameInfo.gameSettings.getPlayerToMove();
        PlayerColor joiningPlayerColor = startingClr.getOpponent();//no player's opp is no player
        txt = "%s %s (%s)".formatted(gameInfo.creatorUsername, joiningPlayerColor == PlayerColor.NO_PLAYER ? "Random" : joiningPlayerColor, gameInfo.gameId);
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
}
