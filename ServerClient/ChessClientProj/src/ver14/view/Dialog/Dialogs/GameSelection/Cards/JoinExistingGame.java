package ver14.view.Dialog.Dialogs.GameSelection.Cards;

import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.Selectable;

public class JoinExistingGame extends SyncedGamesList {

    public JoinExistingGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Join Existing Game"), SyncedListType.JOINABLE_GAMES, parent, gameSettings, GameSettings.GameType.JOIN_EXISTING);
    }

    @Override
    public DialogCard createCard() {
        DialogCard card = super.createCard();
        card.addDefaultValueBtn("Join Random Game", () -> {
            Selectable val = getRandom();
            if (val == null) {

            } else {
                onSelect.callback(val);
                onOk();
            }
        });
        return card;
    }
}
