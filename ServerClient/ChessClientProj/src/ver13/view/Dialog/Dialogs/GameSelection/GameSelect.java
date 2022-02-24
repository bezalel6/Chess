package ver13.view.Dialog.Dialogs.GameSelection;

import ver13.SharedClasses.GameSettings;
import ver13.SharedClasses.networking.AppSocket;
import ver13.view.Dialog.Cards.DialogCard;
import ver13.view.Dialog.Dialog;
import ver13.view.Dialog.DialogProperties;
import ver13.view.Dialog.Dialogs.GameSelection.Cards.GameCreation.GameCreation;
import ver13.view.Dialog.Dialogs.GameSelection.Cards.JoinExistingGame;
import ver13.view.Dialog.Dialogs.GameSelection.Cards.ResumeUnfinishedGame;

import java.awt.*;

public class GameSelect extends Dialog {
    private final GameSettings gameSettings;


    public GameSelect(AppSocket socketToServer, DialogProperties properties, Component parentWin) {
        super(socketToServer, properties, parentWin);
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
