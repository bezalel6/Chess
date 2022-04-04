package ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver14.SharedClasses.Game.GameSettings;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.GameSelectionCard;
import ver14.view.Dialog.Dialogs.GameSelection.Components.PlayerColors;
import ver14.view.Dialog.Dialogs.GameSelection.Components.TimeFormats;

public class GameCreation extends GameSelectionCard {

    public GameCreation(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Create New Game"), parentDialog, gameSettings, GameSettings.GameType.CREATE_NEW);
        addDialogComponent(new PlayerColors(parentDialog, gameSettings));
        addDialogComponent(new TimeFormats(parentDialog, gameSettings));
        addNavigationTo(new StartFromPosition(parentDialog, gameSettings));
        addNavigationTo(new GameVsAi(parentDialog, gameSettings));

        addDefaultValueBtn("Create Game", () -> {
            gameSettings.initDefault1v1();
            onOk();
        });
        addDefaultValueBtn("Create Game VS Ai", () -> {
            gameSettings.initDefault1vAi();
            onOk();
        });
    }
}
