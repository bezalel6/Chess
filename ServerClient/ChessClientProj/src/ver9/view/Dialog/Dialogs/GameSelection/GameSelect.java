package ver9.view.Dialog.Dialogs.GameSelection;

import ver9.SharedClasses.GameSettings;
import ver9.SharedClasses.Sync.SyncableItem;
import ver9.SharedClasses.Sync.SyncedItems;
import ver9.SharedClasses.networking.AppSocket;
import ver9.view.Dialog.Cards.CardHeader;
import ver9.view.Dialog.Cards.DialogCard;
import ver9.view.Dialog.Cards.SimpleDialogCard;
import ver9.view.Dialog.Dialog;
import ver9.view.Dialog.Dialogs.GameSelection.Cards.GameCreation.GameCreation;
import ver9.view.Dialog.Dialogs.GameSelection.Cards.JoinExistingGame;
import ver9.view.Dialog.Dialogs.GameSelection.Cards.ResumeUnfinishedGame;

public class GameSelect extends Dialog {
    private final GameSettings gameSettings;

    public JoinExistingGame existingGames;

    public GameSelect(AppSocket socketToServer, String err) {
        super(socketToServer, "Game Selection", err);
        gameSettings = new GameSettings();
        existingGames = new JoinExistingGame(this, gameSettings);
        DialogCard[] cards = new DialogCard[]{
                new GameCreation(this, gameSettings),
                SimpleDialogCard.create(existingGames, this),
                SimpleDialogCard.create(new ResumeUnfinishedGame(this, gameSettings), this)
        };

        navigationCardSetup(new CardHeader("Select Game"), cards);
    }

    public static void main(String[] args) {
        GameSelect gameSelect = new GameSelect(null, "");
        SyncedItems items = SyncedItems.exampleGames1;
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                items.remove(((SyncableItem) (items.values().toArray()[0])).ID());
                gameSelect.existingGames.sync(items);
                System.out.println("synced");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        gameSelect.start();
        System.out.println(gameSelect.gameSettings);
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }
}
