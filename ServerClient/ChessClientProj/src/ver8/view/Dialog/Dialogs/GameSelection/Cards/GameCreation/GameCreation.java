package ver8.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver8.SharedClasses.GameSettings;
import ver8.view.Dialog.Cards.CardHeader;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Dialogs.GameSelection.Cards.GameSelectionCard;
import ver8.view.Dialog.Dialogs.GameSelection.Components.PlayerColors;
import ver8.view.Dialog.Dialogs.GameSelection.Components.TimeFormats;

public class GameCreation extends GameSelectionCard {
    public GameCreation(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Create New Game"), parentDialog, gameSettings, GameSettings.GameType.CREATE_NEW);
        addDialogComponent(new PlayerColors(parentDialog, gameSettings));
        addNavigationTo(new StartFromPosition(parentDialog, gameSettings));
        addDialogComponent(new TimeFormats(parentDialog, gameSettings));
        addNavigationTo(new GameVsAi(parentDialog, gameSettings));
    }
}
