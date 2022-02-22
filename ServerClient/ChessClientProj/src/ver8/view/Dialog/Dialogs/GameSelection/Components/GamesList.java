package ver8.view.Dialog.Dialogs.GameSelection.Components;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.SavedGames.GameInfo;
import ver8.view.Dialog.Components.ListComponent;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Selectables.Game;
import ver8.view.OldDialogs.Header;

import java.util.Collection;

public class GamesList extends ListComponent {
    private final GameSettings gameSettings;

    public GamesList(Collection<GameInfo> games, GameSettings gameSettings, Dialog parent) {
        super(4, new Header("Select Game"), parent);
        this.gameSettings = gameSettings;

        for (GameInfo gameInfo : games) {
            Game game = new Game(gameInfo, null);
            addComponent(game);
        }
    }

    @Override
    protected void onSelected() {
        String id = null;
        if (selected != null) {
            id = ((Game) selected).gameInfo.gameId;
        }
        gameSettings.setJoiningToGameID(id);
    }
}
