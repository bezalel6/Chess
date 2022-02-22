package ver8.view.Dialog.Dialogs.GameSelection.Components;

import ver8.SharedClasses.GameSetup.AiParameters;
import ver8.SharedClasses.TimeFormat;
import ver8.view.Dialog.Components.ListComponent;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Selectables.AiDifficulty;
import ver8.view.Dialog.WinPnl;
import ver8.view.OldDialogs.Header;

public class AiDifficulties extends ListComponent {
    private final AiParameters aiParameters;

    public AiDifficulties(Dialog parent, AiParameters aiParameters) {
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
