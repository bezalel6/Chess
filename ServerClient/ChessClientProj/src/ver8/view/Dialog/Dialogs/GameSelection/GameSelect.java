package ver8.view.Dialog.Dialogs.GameSelection;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.networking.AppSocket;
import ver8.view.Dialog.Cards.CardHeader;
import ver8.view.Dialog.Cards.DialogCard;
import ver8.view.Dialog.Cards.SimpleDialogCard;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Dialogs.GameSelection.Cards.GameCreation.GameCreation;
import ver8.view.Dialog.Dialogs.GameSelection.Cards.JoinExistingGame;
import ver8.view.Dialog.Dialogs.GameSelection.Cards.ResumeUnfinishedGame;

public class GameSelect extends Dialog {
    protected final GameSettings gameSettings;

    public GameSelect(AppSocket socketToServer) {
        super(socketToServer, "Game Selection");
        gameSettings = new GameSettings();

        DialogCard[] cards = new DialogCard[]{
                new GameCreation(this, gameSettings),
                SimpleDialogCard.create(new JoinExistingGame(this, gameSettings), this),
                SimpleDialogCard.create(new ResumeUnfinishedGame(this, gameSettings), this)
        };

        navigationCardSetup(new CardHeader("Select Game"), cards);
    }


    public static void main(String[] args) {
        GameSelect gameSelect = new GameSelect(null);
        System.out.println(gameSelect.gameSettings);
    }
}
