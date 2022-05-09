package ver14.view.Dialog.Dialogs.GameSelection.Components;

import ver14.SharedClasses.Game.GameSetup.AISettings;
import ver14.SharedClasses.Game.GameSetup.AiType;
import ver14.view.Dialog.Components.ListComponent;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.SelectableAiType;
import ver14.view.Dialog.WinPnl;

/**
 * Ai types.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class AiTypes extends ListComponent {
    /**
     * The Ai parameters.
     */
    private final AISettings aiParameters;

    /**
     * Instantiates a new Ai types.
     *
     * @param parent       the parent
     * @param aiParameters the ai parameters
     */
    public AiTypes(Parent parent, AISettings aiParameters) {
        super(WinPnl.ALL_IN_ONE_ROW, new Header("Select Ai Type"), parent);
        this.aiParameters = aiParameters;
        addComponents(SelectableAiType.selectableAiTypes());
    }

    @Override
    protected void onSelected() {
        AiType type = null;
        if (selected != null) {
            type = ((SelectableAiType) selected).aiType();
        }
        aiParameters.setAiType(type);
    }
}
