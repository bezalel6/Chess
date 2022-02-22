package ver11.view.Dialog.Dialogs.GameSelection.Components;

import ver11.SharedClasses.GameSetup.AiParameters;
import ver11.view.Dialog.Components.ListComponent;
import ver11.view.Dialog.Components.Parent;
import ver11.view.Dialog.Dialogs.Header;
import ver11.view.Dialog.Selectables.AiType;
import ver11.view.Dialog.WinPnl;

public class AiTypes extends ListComponent {
    private final AiParameters aiParameters;

    public AiTypes(Parent parent, AiParameters aiParameters) {
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
