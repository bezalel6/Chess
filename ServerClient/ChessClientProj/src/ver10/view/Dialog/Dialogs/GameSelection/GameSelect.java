package ver10.view.Dialog.Dialogs.GameSelection;

import ver10.SharedClasses.GameSettings;
import ver10.SharedClasses.networking.AppSocket;
import ver10.view.Dialog.Cards.DialogCard;
import ver10.view.Dialog.Dialog;
import ver10.view.Dialog.DialogProperties;
import ver10.view.Dialog.Dialogs.GameSelection.Cards.GameCreation.GameCreation;
import ver10.view.Dialog.Dialogs.GameSelection.Cards.JoinExistingGame;
import ver10.view.Dialog.Dialogs.GameSelection.Cards.ResumeUnfinishedGame;

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
