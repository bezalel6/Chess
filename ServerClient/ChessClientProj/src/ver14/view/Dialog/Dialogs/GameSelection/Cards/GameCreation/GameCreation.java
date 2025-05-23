package ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.DialogFields.Slider.TimeFormatSlider;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation.StartFromPosition.StartFromPosition;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.GameSelectionCard;
import ver14.view.Dialog.Dialogs.GameSelection.Components.PlayerColors;

/**
 * Game creation card.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class GameCreation extends GameSelectionCard {

    /**
     * Instantiates a new Game creation.
     *
     * @param parentDialog the parent dialog
     * @param gameSettings the game settings
     */
    public GameCreation(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Create New Game"), parentDialog, gameSettings, ver14.SharedClasses.Game.GameSetup.GameType.CREATE_NEW);
        addDialogComponent(new PlayerColors(parentDialog, gameSettings));
        addDialogComponent(new TimeFormatSlider(parentDialog, gameSettings));
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
