package ver8.view.Dialog.Dialogs.GameSelection.Components;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.PlayerColor;
import ver8.view.Dialog.Components.ListComponent;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Selectables.SelectablePlayerColor;
import ver8.view.Dialog.WinPnl;
import ver8.view.OldDialogs.Header;

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
