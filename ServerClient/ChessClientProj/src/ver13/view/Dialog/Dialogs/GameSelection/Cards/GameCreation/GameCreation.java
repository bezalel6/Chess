package ver13.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver13.SharedClasses.GameSettings;
import ver13.view.Dialog.Cards.CardHeader;
import ver13.view.Dialog.Dialog;
import ver13.view.Dialog.Dialogs.GameSelection.Cards.GameSelectionCard;
import ver13.view.Dialog.Dialogs.GameSelection.Components.PlayerColors;
import ver13.view.Dialog.Dialogs.GameSelection.Components.TimeFormats;

public class GameCreation extends GameSelectionCard {
    public GameCreation(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Create New Game"), parentDialog, gameSettings, GameSettings.GameType.CREATE_NEW);
        addDialogComponent(new PlayerColors(parentDialog, gameSettings));
        addDialogComponent(new TimeFormats(parentDialog, gameSettings));
        addNavigationTo(new StartFromPosition(parentDialog, gameSettings));
        addNavigationTo(new GameVsAi(parentDialog, gameSettings));
    }
}
