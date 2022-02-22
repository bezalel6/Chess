package ver8.view.Wishfull.dialogs.GameSelect.Selectables;

import ver8.SharedClasses.SavedGames.GameInfo;
import ver8.view.GameIconsGenerator;
import ver8.view.IconManager;
import ver8.view.Wishfull.dialogs.Selectable;

import javax.swing.*;
import java.util.Objects;

public class SelectableGame implements Selectable {
    public final GameInfo gameInfo;
    private final String txt;
    private ImageIcon icon = null;


    /**
     * no icon
     *
     * @param gameInfo
     */
    public SelectableGame(GameInfo gameInfo) {
        this(gameInfo, null);

    }

    /**
     * @param gameInfo
     * @param iconSize - null for no icon
     */
    public SelectableGame(GameInfo gameInfo, IconManager.Size iconSize) {
        this.gameInfo = gameInfo;
        txt = gameInfo.getGameDesc();
        if (iconSize != null && gameInfo.gameSettings.getFen() != null)
            icon = GameIconsGenerator.generate(gameInfo.gameSettings.getFen(), gameInfo.getJoiningPlayerColor(), iconSize);

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
