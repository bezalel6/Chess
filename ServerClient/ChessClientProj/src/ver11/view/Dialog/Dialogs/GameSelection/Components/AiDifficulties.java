package ver11.view.Dialog.Dialogs.GameSelection.Components;

import ver11.SharedClasses.GameSetup.AiParameters;
import ver11.SharedClasses.TimeFormat;
import ver11.view.Dialog.Components.ListComponent;
import ver11.view.Dialog.Components.Parent;
import ver11.view.Dialog.Dialogs.Header;
import ver11.view.Dialog.Selectables.AiDifficulty;
import ver11.view.Dialog.WinPnl;

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
