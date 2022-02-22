package ver8.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver8.SharedClasses.GameSettings;
import ver8.view.Dialog.Cards.CardHeader;
import ver8.view.Dialog.Cards.DialogCard;
import ver8.view.Dialog.Dialog;

public abstract class GameCreationCard extends DialogCard {
    protected final GameSettings gameSettings;

    public GameCreationCard(CardHeader cardHeader, Dialog parentDialog, GameSettings gameSettings) {
        super(cardHeader, parentDialog);
        this.gameSettings = gameSettings;
    }
}
