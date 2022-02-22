package ver8.view.Dialog.Dialogs.GameSelection.Components;

import ver8.SharedClasses.GameSetup.AiParameters;
import ver8.view.Dialog.Components.ListComponent;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Selectables.AiType;
import ver8.view.Dialog.WinPnl;
import ver8.view.OldDialogs.Header;

public class AiTypes extends ListComponent {
    private final AiParameters aiParameters;

    public AiTypes(Dialog parent, AiParameters aiParameters) {
        super(WinPnl.ALL_IN_ONE_ROW, new Header("Select Ai Type"), parent);
        this.aiParameters = aiParameters;
        addComponents(AiType.selectableAiTypes());
    }

    @Override
    protected void onSelected() {
        AiParameters.AiType type = null;
        if (selected != null) {
            type = ((AiType) selected).aiType;
        }
        aiParameters.setAiType(type);
    }
}
