package ver14.view.Dialog.Dialogs.GameSelection.Components;

import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.TimeFormat;
import ver14.view.Dialog.Components.ListComponent;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.AiDifficulty;
import ver14.view.Dialog.WinPnl;

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
