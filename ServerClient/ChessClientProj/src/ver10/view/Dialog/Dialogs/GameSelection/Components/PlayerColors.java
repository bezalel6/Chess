package ver10.view.Dialog.Dialogs.GameSelection.Components;

import ver10.view.Dialog.Components.ListComponent;
import ver10.view.Dialog.Dialog;
import ver10.view.Dialog.Dialogs.Header;
import ver10.view.Dialog.Selectables.SelectablePlayerColor;
import ver10.view.Dialog.WinPnl;
import ver10.SharedClasses.GameSettings;
import ver10.SharedClasses.PlayerColor;

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
