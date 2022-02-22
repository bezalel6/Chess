package ver12.view.Dialog.Dialogs.GameSelection.Components;

import ver12.SharedClasses.GameSetup.AiParameters;
import ver12.SharedClasses.TimeFormat;
import ver12.view.Dialog.Components.ListComponent;
import ver12.view.Dialog.Components.Parent;
import ver12.view.Dialog.Dialogs.Header;
import ver12.view.Dialog.Selectables.AiDifficulty;
import ver12.view.Dialog.WinPnl;

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
