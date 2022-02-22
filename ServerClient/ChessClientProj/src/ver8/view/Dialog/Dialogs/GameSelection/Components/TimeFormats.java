package ver8.view.Dialog.Dialogs.GameSelection.Components;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.TimeFormat;
import ver8.view.Dialog.Components.ListComponent;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Selectables.SelectableTimeFormat;
import ver8.view.Dialog.WinPnl;
import ver8.view.OldDialogs.Header;

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
