package ver8.view.Dialog.Selectables;

import ver8.SharedClasses.PlayerColor;
import ver8.SharedClasses.SavedGames.GameInfo;
import ver8.view.GameIconsGenerator;
import ver8.view.IconManager;

import javax.swing.*;
import java.util.Objects;

public class Game implements Selectable {
    public final GameInfo gameInfo;
    private final String txt;
    private ImageIcon icon = null;

    public Game(GameInfo gameInfo, IconManager.Size iconSize) {
        this.gameInfo = gameInfo;
        PlayerColor startingClr = gameInfo.gameSettings.getPlayerToMove();
        PlayerColor joiningPlayerColor = startingClr.getOpponent();//no player's opp is itself
        txt = "%s %s (%s)".formatted(gameInfo.creatorUsername, joiningPlayerColor == PlayerColor.NO_PLAYER ? "Random" : joiningPlayerColor, gameInfo.gameId);
        if (iconSize != null && gameInfo.gameSettings.getFen() != null)
            icon = GameIconsGenerator.generate(gameInfo.gameSettings.getFen(), joiningPlayerColor, iconSize);

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