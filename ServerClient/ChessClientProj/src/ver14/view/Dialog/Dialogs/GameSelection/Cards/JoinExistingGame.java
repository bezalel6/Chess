package ver14.view.Dialog.Dialogs.GameSelection.Cards;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.GameSelection.Components.SyncedGamesList;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.Selectable;

/**
 * represents a synchronized list of games that are available to join to.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class JoinExistingGame extends SyncedGamesList {

    /**
     * Instantiates a new Join existing game.
     *
     * @param parent       the parent
     * @param gameSettings the game settings
     */
    public JoinExistingGame(Parent parent, GameSettings gameSettings) {
        super(new Header("Join Existing Game"), SyncedListType.JOINABLE_GAMES, parent, gameSettings, GameSettings.GameType.JOIN_EXISTING);
    }

    @Override
    public DialogCard createCard() {
        DialogCard card = super.createCard();
        card.setAdvancedSettingsStr("Join Specific Game");
        card.addDefaultValueBtn("Join Random Game", () -> {
            Selectable val = getRandom();
            if (val == null) {
                parent.dialogWideErr("There are no available games");
            } else {
                onSelect.callback(val);
                onOk();
            }
        });
        return card;
    }
}
