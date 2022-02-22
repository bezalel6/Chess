package ver10.view.Dialog.Dialogs.GameSelection.Components;

import ver10.view.Dialog.Components.ListComponent;
import ver10.view.Dialog.Dialog;
import ver10.view.Dialog.Dialogs.Header;
import ver10.view.Dialog.Selectables.SelectableTimeFormat;
import ver10.view.Dialog.WinPnl;
import ver10.SharedClasses.GameSettings;
import ver10.SharedClasses.TimeFormat;

public class TimeFormats extends ListComponent {
    private final GameSettings gameSettings;

    public TimeFormats(Dialog parent, GameSettings gameSettings) {
        super(WinPnl.ALL_IN_ONE_ROW, new Header("Select Time Format"), parent);
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
