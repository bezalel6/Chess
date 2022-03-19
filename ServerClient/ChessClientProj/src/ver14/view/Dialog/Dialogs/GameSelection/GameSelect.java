package ver14.view.Dialog.Dialogs.GameSelection;

import ver14.SharedClasses.Game.GameSettings;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.DialogProperties.Properties;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation.GameCreation;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.JoinExistingGame;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.ResumeUnfinishedGame;

public class GameSelect extends Dialog {
    private final GameSettings gameSettings;


    public GameSelect(Properties properties) {
        super(properties);
        gameSettings = new GameSettings();
        DialogCard[] cards = new DialogCard[]{
                new GameCreation(this, gameSettings),
                new JoinExistingGame(this, gameSettings).createCard(),
                new ResumeUnfinishedGame(this, gameSettings).createCard()
        };

        navigationCardSetup(cards);
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }
}
