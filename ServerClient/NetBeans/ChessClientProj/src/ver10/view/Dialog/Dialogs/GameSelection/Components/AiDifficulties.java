package ver10.view.Dialog.Dialogs.GameSelection.Components;

import ver10.view.Dialog.Components.ListComponent;
import ver10.view.Dialog.Components.Parent;
import ver10.view.Dialog.Dialogs.Header;
import ver10.view.Dialog.Selectables.AiDifficulty;
import ver10.view.Dialog.WinPnl;
import ver10.SharedClasses.GameSetup.AiParameters;
import ver10.SharedClasses.TimeFormat;

public class AiDifficulties extends ListComponent {
    private final AiParameters aiParameters;

    public AiDifficulties(Parent parent, AiParameters aiParameters) {
        super(WinPnl.ALL_IN_ONE_ROW, new Header("Select Ai Difficulty"), parent);
        this.aiParameters = aiParameters;
        addComponents(AiDifficulty.values());
    }

    @Override
    protected void onSelected() {
        TimeFormat timeFormat = null;
        if (selected != null) {
            timeFormat = ((AiDifficulty) selected).timeFormat;
        }
        aiParameters.setMoveSearchTimeout(timeFormat);
    }
}
