package ver9.view.Dialog.Dialogs.GameSelection.Cards;

import ver9.SharedClasses.GameSettings;
import ver9.view.Dialog.Cards.CardHeader;
import ver9.view.Dialog.Cards.DialogCard;
import ver9.view.Dialog.Dialog;

public abstract class GameSelectionCard extends DialogCard {
    protected final GameSettings gameSettings;
    protected final GameSettings.GameType gameType;

    public GameSelectionCard(CardHeader cardHeader, Dialog parentDialog, GameSettings gameSettings, GameSettings.GameType gameType) {
        super(cardHeader, parentDialog);
        this.gameSettings = gameSettings;
        this.gameType = gameType;
    }


    @Override
    public void onOk() {
        gameSettings.setGameType(gameType);
        super.onOk();
    }
}
