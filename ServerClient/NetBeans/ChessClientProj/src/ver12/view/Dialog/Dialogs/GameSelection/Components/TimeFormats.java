package ver12.view.Dialog.Dialogs.GameSelection.Components;

import ver12.SharedClasses.GameSettings;
import ver12.SharedClasses.TimeFormat;
import ver12.view.Dialog.Components.ListComponent;
import ver12.view.Dialog.Dialog;
import ver12.view.Dialog.Dialogs.Header;
import ver12.view.Dialog.Selectables.SelectableTimeFormat;
import ver12.view.Dialog.WinPnl;

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
