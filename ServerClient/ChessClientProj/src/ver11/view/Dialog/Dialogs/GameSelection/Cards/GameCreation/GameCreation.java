package ver11.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver11.SharedClasses.GameSettings;
import ver11.view.Dialog.Cards.CardHeader;
import ver11.view.Dialog.Dialog;
import ver11.view.Dialog.Dialogs.GameSelection.Cards.GameSelectionCard;
import ver11.view.Dialog.Dialogs.GameSelection.Components.PlayerColors;
import ver11.view.Dialog.Dialogs.GameSelection.Components.TimeFormats;

public class GameCreation extends GameSelectionCard {
    public GameCreation(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Create New Game"), parentDialog, gameSettings, GameSettings.GameType.CREATE_NEW);
        addDialogComponent(new PlayerColors(parentDialog, gameSettings));
        addDialogComponent(new TimeFormats(parentDialog, gameSettings));
        addNavigationTo(new StartFromPosition(parentDialog, gameSettings));
        addNavigationTo(new GameVsAi(parentDialog, gameSettings));
    }
}
