package ver14.view.Dialog.Dialogs.GameSelection;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Utils.ArrUtils;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation.GameCreation;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.JoinExistingGame;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.ResumeUnfinishedGame;
import ver14.view.Dialog.Properties;
import ver14.view.IconManager.Size;

public class GameSelect extends Dialog {
    private final GameSettings gameSettings;


    public GameSelect(Properties properties) {
        super(properties);
        gameSettings = new GameSettings();
        DialogCard[] cards = new DialogCard[]{
                new GameCreation(this, gameSettings),
                new JoinExistingGame(this, gameSettings).createCard(),
        };
        if (!properties.getLoginInfo().isGuest()) {
            cards = ArrUtils.concat(cards, new ResumeUnfinishedGame(this, gameSettings).createCard());
        }

        navigationCardSetup(new Size(400, 500), cards);
    }


    public GameSettings getGameSettings() {
        return gameSettings;
    }
}
