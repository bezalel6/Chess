package ver9.view.Dialog.Dialogs.GameSelection.Components;

import ver9.SharedClasses.GameSettings;
import ver9.SharedClasses.TimeFormat;
import ver9.view.Dialog.Components.ListComponent;
import ver9.view.Dialog.Dialog;
import ver9.view.Dialog.Dialogs.Header;
import ver9.view.Dialog.Selectables.SelectableTimeFormat;
import ver9.view.Dialog.WinPnl;

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
