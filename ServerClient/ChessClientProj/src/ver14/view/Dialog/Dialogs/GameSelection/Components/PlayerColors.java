package ver14.view.Dialog.Dialogs.GameSelection.Components;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.view.Dialog.Components.ListComponent;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.SelectablePlayerColor;
import ver14.view.Dialog.WinPnl;

/**
 * Player colors.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class PlayerColors extends ListComponent {
    /**
     * The Game settings.
     */
    private final GameSettings gameSettings;

    /**
     * Instantiates a new Player colors.
     *
     * @param parent       the parent
     * @param gameSettings the game settings
     */
    public PlayerColors(Dialog parent, GameSettings gameSettings) {
        super(WinPnl.ALL_IN_ONE_ROW, new Header("Select Player Color"), parent);
        this.gameSettings = gameSettings;
        addComponents(SelectablePlayerColor.values());
        setValue(SelectablePlayerColor.WHITE);
    }

    @Override
    protected void onSelected() {
        PlayerColor clr = null;
        if (selected != null) {
            clr = ((SelectablePlayerColor) selected).playerColor;
        }
        gameSettings.setPlayerToMove(clr);
    }
}
