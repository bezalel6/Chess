package ver14.view.Dialog.Dialogs.GameSelection.Components;

import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.TimeFormat;
import ver14.view.Dialog.Components.ListComponent;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.SelectableTimeFormat;
import ver14.view.Dialog.WinPnl;

public class TimeFormats extends ListComponent {
    private final GameSettings gameSettings;

    public TimeFormats(Dialog parent, GameSettings gameSettings) {
        super(WinPnl.ALL_IN_ONE_ROW, new Header("Time Per Move"), parent);
        this.gameSettings = gameSettings;
        addComponents(SelectableTimeFormat.values());
    }

    @Override
    protected void onSelected() {
        TimeFormat timeFormat = null;
        if (selected != null) {
            timeFormat = ((SelectableTimeFormat) selected).timeFormat;
        }
        gameSettings.setTimeFormat(timeFormat);
    }
}
