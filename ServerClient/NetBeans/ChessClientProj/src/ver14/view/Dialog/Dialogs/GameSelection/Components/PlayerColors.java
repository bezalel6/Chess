package ver14.view.Dialog.Dialogs.GameSelection.Components;

import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.view.Dialog.Components.ListComponent;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.SelectablePlayerColor;
import ver14.view.Dialog.WinPnl;

public class PlayerColors extends ListComponent {
    private final GameSettings gameSettings;

    public PlayerColors(Dialog parent, GameSettings gameSettings) {
        super(WinPnl.ALL_IN_ONE_ROW, new Header("Select Player Color"), parent);
        this.gameSettings = gameSettings;
        addComponents(SelectablePlayerColor.values());
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
