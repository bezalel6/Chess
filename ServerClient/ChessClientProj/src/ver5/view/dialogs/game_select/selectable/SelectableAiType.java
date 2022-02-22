package ver5.view.dialogs.game_select.selectable;

import ver5.SharedClasses.GameSetup.AiParameters;
import ver5.view.dialogs.Selectable;

import javax.swing.*;

public class SelectableAiType implements Selectable {

    public final AiParameters.AiType aiType;

    public SelectableAiType(AiParameters.AiType aiType) {
        this.aiType = aiType;
    }

    public static SelectableAiType[] selectableAiTypes() {
        SelectableAiType[] ret = new SelectableAiType[AiParameters.AiType.values().length];
        for (int i = 0; i < AiParameters.AiType.values().length; i++) {
            ret[i] = new SelectableAiType(AiParameters.AiType.values()[i]);
        }
        return ret;
    }


    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getText() {
        return aiType.name();
    }
}
