package ver10.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver10.view.Dialog.Cards.CardHeader;
import ver10.view.Dialog.Dialog;
import ver10.view.Dialog.Dialogs.GameSelection.Cards.GameSelectionCard;
import ver10.view.Dialog.Dialogs.GameSelection.Components.PlayerColors;
import ver10.view.Dialog.Dialogs.GameSelection.Components.TimeFormats;
import ver10.SharedClasses.GameSettings;

public class GameCreation extends GameSelectionCard {
    public GameCreation(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Create New Game"), parentDialog, gameSettings, GameSettings.GameType.CREATE_NEW);
        addDialogComponent(new PlayerColors(parentDialog, gameSettings));
        addDialogComponent(new TimeFormats(parentDialog, gameSettings));
        addNavigationTo(new StartFromPosition(parentDialog, gameSettings));
        addNavigationTo(new GameVsAi(parentDialog, gameSettings));
    }
}
