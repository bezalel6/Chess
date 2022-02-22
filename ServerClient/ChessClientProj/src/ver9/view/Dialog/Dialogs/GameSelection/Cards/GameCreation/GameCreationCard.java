package ver9.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver9.SharedClasses.GameSettings;
import ver9.view.Dialog.Cards.CardHeader;
import ver9.view.Dialog.Cards.DialogCard;
import ver9.view.Dialog.Dialog;

public abstract class GameCreationCard extends DialogCard {
    protected final GameSettings gameSettings;

    public GameCreationCard(CardHeader cardHeader, Dialog parentDialog, GameSettings gameSettings) {
        super(cardHeader, parentDialog);
        this.gameSettings = gameSettings;
    }
}
