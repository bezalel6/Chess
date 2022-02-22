package ver11.view.Dialog.Dialogs.GameSelection.Components;

import ver11.SharedClasses.GameSettings;
import ver11.SharedClasses.PlayerColor;
import ver11.view.Dialog.Components.ListComponent;
import ver11.view.Dialog.Dialog;
import ver11.view.Dialog.Dialogs.Header;
import ver11.view.Dialog.Selectables.SelectablePlayerColor;
import ver11.view.Dialog.WinPnl;

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
